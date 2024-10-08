package com.talys.calendar.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.talys.calendar.models.EventDto;
import com.talys.calendar.services.EventService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/calendar/events/admin")
@Validated
public class EventController {
    @Autowired
    private EventService eventService;

    @GetMapping
    public List<EventDto> getAllEvents() {
        return eventService.getAllEvents();
    }

    @GetMapping("/{id}")
    public EventDto getEventById(@PathVariable Integer id) {
        return eventService.getEventById(id);
    }

    @PostMapping
    public EventDto createEvent(@Valid @RequestBody EventDto eventDto) {
        return eventService.createEvent(eventDto);
    }

    @PutMapping("/{id}")
    public EventDto updateEvent(@PathVariable Integer id, @Valid @RequestBody EventDto eventDto) {
        return eventService.updateEvent(id, eventDto);
    }

    @DeleteMapping("/{id}")
    public void deleteEvent(@PathVariable Integer id) {
        eventService.deleteEvent(id);
    }
    

    @GetMapping("/user-events")
    public List<EventDto> getEventsForCurrentUser() {
        return eventService.getEventsForCurrentUser();
    }
    
    @GetMapping("/user-events-id/{id}")
    public List<EventDto> getEventsForCurrentUserByid(@PathVariable Integer id){
        return eventService.getEventsForCurrentUserByid(id);

    }
    
    @GetMapping("/iscompleted")
    public List<EventDto> getCompletedEvents(){
    	return eventService.getCompletedEvents();
    }
    
    @GetMapping("/isnoncompleted")
    public List<EventDto> getNonCompletedEvents(){
    	return eventService.getNonCompletedEvents();
    }
    
    @GetMapping("/isnoncompletedByuser/{id}")
    public List<EventDto> getNonCompletedEventsForUser(@PathVariable Integer id){
    	return eventService.getNonCompletedEventsForUser(id);
    }
    @GetMapping("/iscompletedByuser/{id}")
    public List<EventDto> getCompletedEventsForUser(@PathVariable Integer id){
    	return eventService.getCompletedEventsForUser(id);
    }


    

}
