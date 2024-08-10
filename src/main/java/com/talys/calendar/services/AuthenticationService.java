package com.talys.calendar.services;

import java.util.HashSet;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import com.talys.calendar.exceptions.AppException;

import com.talys.calendar.enums.Role;
import com.talys.calendar.models.AuthenticationRequest;
import com.talys.calendar.models.RegisterRequest;
import com.talys.calendar.models.User;
import com.talys.calendar.repositories.UserRepository;
import com.talys.calendar.response.AuthenticationResponse;

import lombok.RequiredArgsConstructor;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

@Service
@RequiredArgsConstructor
public class AuthenticationService {
	
	@Autowired 
	private UserRepository UserRepo;
	@Autowired
	private PasswordEncoder passwordEncoder;
	@Autowired
	private JwtService jwtService; 
	@Autowired
	private AuthenticationManager authenticationManager;
	
    public AuthenticationResponse register(RegisterRequest request) {
        var user = User.builder()
            .firstname(request.getFirstname())
            .lastname(request.getLastname())
            .email(request.getEmail())
            .password(passwordEncoder.encode(request.getPassword()))
            .role(request.getRole())  // Use the role from the request
            .build();
        UserRepo.save(user);
        var jwtToken = jwtService.generateToken(user);
        
        return AuthenticationResponse.builder()
            .token(jwtToken)
            .role(user.getRole())
			.statusCode(HttpStatus.OK.value())
            .build();
    }

	public AuthenticationResponse authenticate(AuthenticationRequest request) {
		var user = UserRepo.findByEmail(request.getEmail());
		authenticationManager.authenticate(
			new UsernamePasswordAuthenticationToken(
				request.getEmail(),
				request.getPassword()
			)
		);
		
		var jwtToken = jwtService.generateToken(user);
		return AuthenticationResponse.builder()
			.token(jwtToken)
			.role(user.getRole())
			.statusCode(HttpStatus.OK.value())
			.build();
	}	
	
	public List<User> getAllUsers() {
		return UserRepo.findAll();
	}

	public User getUserById(Integer id) {
		return UserRepo.findById(id).orElseThrow(() -> new UsernameNotFoundException("User not found"));
	}
	public User getProfile() {
		Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		String username;
		if (principal instanceof UserDetails) {
			username = ((UserDetails) principal).getUsername();
		} else {
			username = principal.toString();
		}
		return UserRepo.findByEmail(username);
	}


}
