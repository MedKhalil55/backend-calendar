package com.talys.calendar.services;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.talys.calendar.exceptions.AppException;
import com.talys.calendar.mappers.EventDtoMapper;
import com.talys.calendar.models.Event;
import com.talys.calendar.models.EventDto;
import com.talys.calendar.repositories.EventRepository;

@Service
public class EventService {
    @Autowired
    private EventRepository eventRepository;
    @Autowired
    private EventDtoMapper eventDtoMapper;

    public List<EventDto> getAllEvents() {
        return eventRepository.findAll().stream()
                .map(eventDtoMapper::toDto)
                .collect(Collectors.toList());
    }

    public EventDto createEvent(EventDto eventDto) {
        Event event = eventDtoMapper.toEntity(eventDto);
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

        Event updatedEvent = eventRepository.save(event);
        return eventDtoMapper.toDto(updatedEvent);
    }

    public void deleteEvent(Integer id) {
        Event event = eventRepository.findById(id)
                .orElseThrow(() -> new AppException("Event not found with id: " + id, HttpStatus.NOT_FOUND));

        eventRepository.delete(event);
    }
}




