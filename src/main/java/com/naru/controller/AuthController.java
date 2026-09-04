package com.naru.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.naru.dto.LoginRequest;
import com.naru.dto.LoginResponse;
import com.naru.dto.RefreshTokenRequest;
import com.naru.dto.RegisterRequest;
import com.naru.dto.RegisterResponse;
import com.naru.security.JwtService;
import com.naru.service.AuthService;
import com.naru.service.RefreshTokenService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

	private final AuthService authService;
	private final RefreshTokenService refreshTokenService;
	private final JwtService jwtService;

	@PostMapping("/register")
	public RegisterResponse register(@Valid @RequestBody RegisterRequest request) {
		return authService.register(request);
	}

	@PostMapping("/login")
	public LoginResponse login(@Valid @RequestBody LoginRequest request) {
		return authService.login(request);
	}

	@PostMapping("/refresh")
	public LoginResponse refreshToken(@Valid @RequestBody RefreshTokenRequest request) {
		var refreshToken = refreshTokenService.verifyRefreshToken(request.getRefreshToken());
		String newAccessToken = jwtService.generateAccessToken(refreshToken.getUser().getEmail());

		return new LoginResponse(newAccessToken, request.getRefreshToken());
	}

	@PostMapping("/logout")
	public ResponseEntity<?> logout(@RequestBody RefreshTokenRequest request) {
		refreshTokenService.deleteByToken(request.getRefreshToken());
		return ResponseEntity.ok("Logged out successfully");
	}

}
