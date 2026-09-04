package com.naru.service;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.naru.dto.LoginRequest;
import com.naru.dto.LoginResponse;
import com.naru.dto.RegisterRequest;
import com.naru.dto.RegisterResponse;
import com.naru.entity.User;
import com.naru.enums.Role;
import com.naru.repository.UserRepository;
import com.naru.security.JwtService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthService {

	private final UserRepository userRepository;
	private final PasswordEncoder passwordEncoder;

	private final AuthenticationManager authenticationManager;
	private final JwtService jwtService;
	private final RefreshTokenService refreshTokenService;

	public RegisterResponse register(RegisterRequest request) {

		if (userRepository.existsByEmail(request.getEmail())) {
			throw new RuntimeException("Email already registered");
		}
		
		User user = User.builder()
		        .email(request.getEmail())
		        .password(passwordEncoder.encode(request.getPassword()))
		        .role(Role.ROLE_USER)
		        .build();

		userRepository.save(user);

		return new RegisterResponse("User registered successfully");
	}

	public LoginResponse login(LoginRequest request) {

		Authentication authentication = authenticationManager
				.authenticate(new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));

		String accessToken = jwtService.generateAccessToken(request.getEmail());
		String refreshToken = refreshTokenService.createRefreshToken(request.getEmail()).getToken();

		return new LoginResponse(accessToken, refreshToken);
	}

}