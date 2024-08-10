package com.talys.calendar.controllers;


import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.talys.calendar.models.AuthenticationRequest;
import com.talys.calendar.models.RegisterRequest;
import com.talys.calendar.models.User;
import com.talys.calendar.response.AuthenticationResponse;

import com.talys.calendar.services.AuthenticationService;


@RestController
@RequestMapping("/api/auth")
public class AuthenticationController {
	
	@Autowired
	private AuthenticationService authenticationService;
	

	
	@PostMapping("/register")
	public ResponseEntity<AuthenticationResponse> register(
			@RequestBody RegisterRequest request
			){
		
		return ResponseEntity.ok(authenticationService.register(request));
		
	}
	@PostMapping("/authenticate")
	public ResponseEntity<AuthenticationResponse> authenticate(
			@RequestBody AuthenticationRequest request
			){
		return ResponseEntity.ok(authenticationService.authenticate(request));
	}
	@GetMapping("/getUsers")
	public ResponseEntity<List<User>> getAllUsers() {
		return ResponseEntity.ok(authenticationService.getAllUsers());
	}
	
	@GetMapping("/getUsers/{id}")
	public ResponseEntity<User> getUserById(@PathVariable Integer id) {
		return ResponseEntity.ok(authenticationService.getUserById(id));
	}
    @GetMapping("/profile")
    public ResponseEntity<User> getProfile() {
        return ResponseEntity.ok(authenticationService.getProfile());
    }

	

}
