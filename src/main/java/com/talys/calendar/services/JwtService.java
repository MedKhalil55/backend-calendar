package com.talys.calendar.services;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

@Service
public class JwtService {
	
	private static final String SECRET_KEY ="ewdVfFgasYaiCEdvRnIi8qKBbSsMVAMTO7A3oqB1knw49MQmz89x5xJlHTwGmzCZ";

	public String extractUserEmail(String jwt) {
		//extract the subject(email or username...) from the claim
		return extractClaim(jwt , Claims::getSubject);
	}
	
	public <T> T extractClaim(String token ,Function<Claims, T> claimResolver){
		final Claims claims = extractAllClaims(token);
		return claimResolver.apply(claims);
	}
	
	private Claims extractAllClaims(String token) {
		return Jwts
				.parserBuilder()
		        .setSigningKey(getSigningToken())
		        .build()
		        .parseClaimsJws(token)
		        .getBody();
	}

	private Key getSigningToken() {
		byte[] keyBytes = Decoders.BASE64.decode(SECRET_KEY);
		
		return Keys.hmacShaKeyFor(keyBytes);
	}
	
	
	public String generateToken(UserDetails userDetails) {
		return generateToken(new HashMap<>() , userDetails);
	}
	
	public String generateToken(
			   Map<String, Object> extraClaims,
			   UserDetails userDetails
			   )
	  {
		return Jwts 
				.builder()
				.setClaims(extraClaims)
				//pass the email(unique)
				.setSubject(userDetails.getUsername())
				//begin
				.setIssuedAt(new Date(System.currentTimeMillis()))
				//expired
				.setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 24))
				.signWith(getSigningToken(), SignatureAlgorithm.HS256)
				//generate the token
				.compact();
		 
	        }
	
	public boolean isTokenValid(String token, UserDetails userDetails) {
		final String username = extractUserEmail(token);
		return (username.equals(userDetails.getUsername())) && !isTokenExpired(token);
		
	}

	private boolean isTokenExpired(String token) {
		return extractExpiration(token).before(new Date());
	}

	private Date extractExpiration(String token) {
		return extractClaim(token , Claims::getExpiration);
	}
	

}
