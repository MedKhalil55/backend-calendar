package com.talys.calendar.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.talys.calendar.models.Event;

public interface  EventRepository extends JpaRepository<Event, Integer> {
	
}


