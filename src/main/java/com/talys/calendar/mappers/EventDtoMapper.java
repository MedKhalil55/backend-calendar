package com.talys.calendar.mappers;

import org.springframework.stereotype.Component;

import com.talys.calendar.models.Event;
import com.talys.calendar.models.EventDto;

@Component
public class EventDtoMapper {
    public EventDto toDto(Event event) {
        EventDto dto = new EventDto();
        dto.setId(event.getId()); // Set the ID field
        dto.setTitle(event.getTitle());
        dto.setDescription(event.getDescription());
        dto.setStartDateTime(event.getStartDateTime());
        dto.setEndDateTime(event.getEndDateTime());
        dto.setCategory(event.getCategory());
        return dto;
    }

    public Event toEntity(EventDto dto) {
        Event event = new Event();
        event.setId(dto.getId()); // Set the ID field
        event.setTitle(dto.getTitle());
        event.setDescription(dto.getDescription());
        event.setStartDateTime(dto.getStartDateTime());
        event.setEndDateTime(dto.getEndDateTime());
        event.setCategory(dto.getCategory());
        return event;
    }
}
