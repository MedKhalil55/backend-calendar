package com.talys.calendar.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.talys.calendar.models.User;

public interface UserRepository extends JpaRepository<User,Integer>{
	
		User findByEmail(String email);

}
