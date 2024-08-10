package com.talys.calendar.mappers;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.talys.calendar.models.Event;
import com.talys.calendar.models.EventDto;
import com.talys.calendar.models.User;
import com.talys.calendar.repositories.EventRepository;
import com.talys.calendar.repositories.UserRepository;



@Component
public class EventDtoMapper {
	@Autowired 
	private UserRepository UserRepo;

	
    public EventDto toDto(Event event) {
        EventDto dto = new EventDto();
        dto.setId(event.getId());
        dto.setTitle(event.getTitle());
        dto.setDescription(event.getDescription());
        dto.setStartDateTime(event.getStartDateTime());
        dto.setEndDateTime(event.getEndDateTime());
        dto.setCategory(event.getCategory());
        dto.setUserIds(event.getUsers().stream().map(User::getId).collect(Collectors.toSet()));
        dto.setResponsibleUserId(event.getResponsibleUser().getId());
        dto.setIsCompleted(event.getIsCompleted());
        return dto;
    }

    public Event toEntity(EventDto dto) {
        Event event = new Event();
        event.setId(dto.getId());
        event.setTitle(dto.getTitle());
        event.setDescription(dto.getDescription());
        event.setStartDateTime(dto.getStartDateTime());
        event.setEndDateTime(dto.getEndDateTime());
        event.setCategory(dto.getCategory());
        Set<User> users = new HashSet<>(UserRepo.findAllById(dto.getUserIds()));
        event.setUsers(users);
        users.forEach(user -> user.getEvents().add(event));
        User responsibleUser = UserRepo.findById(dto.getResponsibleUserId()).orElse(null);
        event.setResponsibleUser(responsibleUser);
        event.setIsCompleted(dto.getIsCompleted());

        return event;
    }
}
