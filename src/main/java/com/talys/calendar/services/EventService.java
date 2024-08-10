package com.talys.calendar.services;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import com.talys.calendar.enums.Role;
import com.talys.calendar.exceptions.AppException;
import com.talys.calendar.mappers.EventDtoMapper;
import com.talys.calendar.models.Event;
import com.talys.calendar.models.EventDto;
import com.talys.calendar.models.User;
import com.talys.calendar.repositories.EventRepository;
import com.talys.calendar.repositories.UserRepository;

@Service
public class EventService {

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private EventDtoMapper eventDtoMapper;

    @Autowired
    private UserRepository userRepo;
    
    @Autowired
    private AuthenticationService authservice;

    public List<EventDto> getAllEvents() {
        return eventRepository.findAll().stream()
                .map(eventDtoMapper::toDto)
                .collect(Collectors.toList());
    }

    public EventDto createEvent(EventDto eventDto) {
        Event event = eventDtoMapper.toEntity(eventDto);
        Set<User> users = new HashSet<>(userRepo.findAllById(eventDto.getUserIds()));
        event.setUsers(users);
        users.forEach(user -> user.getEvents().add(event));  // Ensure the bi-directional relationship is maintained
        Event savedEvent = eventRepository.save(event);
        return eventDtoMapper.toDto(savedEvent);
    }

    public EventDto getEventById(Integer id) {
        Event event = eventRepository.findById(id)
                .orElseThrow(() -> new AppException("Event not found with id: " + id, HttpStatus.NOT_FOUND));
        return eventDtoMapper.toDto(event);
    }

    public EventDto updateEvent(Integer id, EventDto eventDto) {
        Event event = eventRepository.findById(id)
                .orElseThrow(() -> new AppException("Event not found with id: " + id, HttpStatus.NOT_FOUND));

        event.setTitle(eventDto.getTitle());
        event.setDescription(eventDto.getDescription());
        event.setStartDateTime(eventDto.getStartDateTime());
        event.setEndDateTime(eventDto.getEndDateTime());
        event.setCategory(eventDto.getCategory());
        
        //set assigned users
        Set<User> users = new HashSet<>(userRepo.findAllById(eventDto.getUserIds()));
        event.setUsers(users);
        users.forEach(user -> user.getEvents().add(event));  // Ensure the bi-directional relationship is maintained
        
        // Set responsible user
        User responsibleUser = userRepo.findById(eventDto.getResponsibleUserId())
                .orElseThrow(() -> new AppException("Responsible user not found", HttpStatus.NOT_FOUND));
        event.setResponsibleUser(responsibleUser);
        
        //set iscompleted?
        event.setIsCompleted(eventDto.getIsCompleted());

        Event updatedEvent = eventRepository.save(event);
        return eventDtoMapper.toDto(updatedEvent);
    }

    public void deleteEvent(Integer id) {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String username;
        if (principal instanceof UserDetails) {
            username = ((UserDetails) principal).getUsername();
        } else {
            username = principal.toString();
        }
        
        User currentUser = userRepo.findByEmail(username);

        Event event = eventRepository.findById(id)
                .orElseThrow(() -> new AppException("Event not found with id: " + id, HttpStatus.NOT_FOUND));
        
        Set<User> users = event.getUsers();

        if (currentUser.getRole() == Role.Admin) {
            // Remove references to this event from all associated users
            users.forEach(user -> {
                user.getEvents().remove(event); // Remove the event from the user's events set
                userRepo.save(user); // Save the user entity to persist changes
            });
            
            eventRepository.delete(event);
        } else if (currentUser.getRole() == Role.User) {
            if (users.contains(currentUser)) {
                users.remove(currentUser); // Remove the user from the event's users set
                event.setUsers(users);
                eventRepository.save(event); // Save the event entity to persist changes
            } else {
                throw new AppException("User is not associated with this event", HttpStatus.FORBIDDEN);
            }
        }
    }


    public List<EventDto> getEventsForCurrentUser() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String username;
        if (principal instanceof UserDetails) {
            username = ((UserDetails) principal).getUsername();
        } else {
            username = principal.toString();
        }

        User currentUser = userRepo.findByEmail(username);

        if (currentUser.getRole() == Role.Admin) {
            // Admin sees all events
            return getAllEvents();
        } else {
            // Regular user sees only their assigned events
            return currentUser.getEvents().stream()
                .map(eventDtoMapper::toDto)
                .collect(Collectors.toList());
        }
    }   
    
    public List<EventDto> getEventsForCurrentUserByid(Integer id ) {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String username;
        if (principal instanceof UserDetails) {
            username = ((UserDetails) principal).getUsername();
        } else {
            username = principal.toString();
        }

        User currentUser = authservice.getUserById(id);
        	return currentUser.getEvents().stream()
                    .map(eventDtoMapper::toDto)
                    .collect(Collectors.toList());
        } 
        
    public EventDto markEventAsCompleted(Integer eventId) {
        Event event = eventRepository.findById(eventId)
               .orElseThrow(() -> new AppException("Event not found with id: " + eventId, HttpStatus.NOT_FOUND));
        
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String username;
        if (principal instanceof UserDetails) {
            username = ((UserDetails) principal).getUsername();
        } else {
            username = principal.toString();
        }

        User currentUser = userRepo.findByEmail(username);
        
        if (!event.getResponsibleUser().equals(currentUser)) {
            throw new AppException("Only the responsible user can mark this event as completed.", HttpStatus.FORBIDDEN);
        }
        
        event.setIsCompleted(!event.getIsCompleted());
        Event updatedEvent = eventRepository.save(event);
        return eventDtoMapper.toDto(updatedEvent);
    }
    public List<EventDto> getCompletedEvents() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String username;
        if (principal instanceof UserDetails) {
            username = ((UserDetails) principal).getUsername();
        } else {
            username = principal.toString();
        }

        User currentUser = userRepo.findByEmail(username);

        if (currentUser.getRole() == Role.Admin) {
            // Admin sees all completed events
            return eventRepository.findAll().stream()
                    .filter(Event::getIsCompleted)
                    .map(eventDtoMapper::toDto)
                    .collect(Collectors.toList());
        } else {
            // Regular user sees their own completed events
            return currentUser.getEvents().stream()
                    .filter(Event::getIsCompleted)
                    .map(eventDtoMapper::toDto)
                    .collect(Collectors.toList());
        }
    }
    
    public List<EventDto> getNonCompletedEvents() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String username;
        if (principal instanceof UserDetails) {
            username = ((UserDetails) principal).getUsername();
        } else {
            username = principal.toString();
        }

        User currentUser = userRepo.findByEmail(username);

        if (currentUser.getRole() == Role.Admin) {
            // Admin sees all non-completed events
            return eventRepository.findAll().stream()
                    .filter(event -> !event.getIsCompleted())
                    .map(eventDtoMapper::toDto)
                    .collect(Collectors.toList());
        } else {
            // Regular user sees their own non-completed events
            return currentUser.getEvents().stream()
                    .filter(event -> !event.getIsCompleted())
                    .map(eventDtoMapper::toDto)
                    .collect(Collectors.toList());
        }
    }
    public List<EventDto> getNonCompletedEventsForUser(Integer id) {

        User currentUser = authservice.getUserById(id);

   
            // Regular user sees their own non-completed events
            return currentUser.getEvents().stream()
                    .filter(event -> !event.getIsCompleted())
                    .map(eventDtoMapper::toDto)
                    .collect(Collectors.toList());
        
    }
    public List<EventDto> getCompletedEventsForUser(Integer id) {

        User currentUser = authservice.getUserById(id);

   
            // Regular user sees their own non-completed events
            return currentUser.getEvents().stream()
                    .filter(Event::getIsCompleted)
                    .map(eventDtoMapper::toDto)
                    .collect(Collectors.toList());
        
    }




    }

	






