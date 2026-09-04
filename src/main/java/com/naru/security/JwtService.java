package com.naru.security;

import java.security.Key;
import java.util.Date;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.naru.entity.User;
import com.naru.repository.UserRepository;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class JwtService {

	private final UserRepository userRepository;
	
	@Value("${jwt.secret}")
	private String secret;

	@Value("${jwt.access.expiration}")
	private long accessTokenExpiration;

	private Key getSigningKey() {
		return Keys.hmacShaKeyFor(secret.getBytes());
	}

	public String generateAccessToken(String email) {

		  User user = userRepository.findByEmail(email)
		            .orElseThrow();
		
		return Jwts.builder().setSubject(email).claim("role", user.getRole().name()).setIssuedAt(new Date())
				.setExpiration(new Date(System.currentTimeMillis() + accessTokenExpiration))
				.signWith(getSigningKey(), SignatureAlgorithm.HS256).compact();  // compact -> convert to string
	}
	
	public String extractUsername(String token) {
	    return Jwts.parserBuilder()
	            .setSigningKey(getSigningKey())
	            .build()
	            .parseClaimsJws(token)
	            .getBody()
	            .getSubject();
	}

	public boolean isTokenValid(String token) {
	    try {
	        Jwts.parserBuilder()
	                .setSigningKey(getSigningKey())
	                .build()
	                .parseClaimsJws(token);
	        return true;
	    } catch (Exception e) {
	        return false;
	    }
	}
	
	public Claims extractAllClaims(String token) {
	    return Jwts.parserBuilder()
	            .setSigningKey(getSigningKey())
	            .build()
	            .parseClaimsJws(token)
	            .getBody();
	}

}