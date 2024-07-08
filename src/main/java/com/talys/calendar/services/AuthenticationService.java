package com.talys.calendar.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Service;

import com.talys.calendar.enums.Role;
import com.talys.calendar.models.RegisterRequest;
import com.talys.calendar.repositories.UserRepository;
import com.talys.calendar.response.AuthenticationResponse;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthenticationService {
	
	@Autowired 
	private UserRepository UserRepo;
	
	 public AuthenticationResponse register(RegisterRequest request) {
		 
		 var user = User.builder()
		     .firstname(request.getFirstname())
		     .lastname(request.getLastname())
		     .email(request.getEmail())
		     .password(passwordEncoder.encode(request.getPassword()))
		     .role(Role.User)
		     .build();
		 
		 return null;
		 
	 }
    
	 public AuthenticationResponse authenticate(RegisterRequest request) {
		 
	 }

}
