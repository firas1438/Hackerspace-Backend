package com.naru.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.naru.dto.LoginRequest;
import com.naru.dto.LoginResponse;
import com.naru.dto.RegisterRequest;
import com.naru.dto.RegisterResponse;
import com.naru.dto.SessionResponse;
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

	private ResponseCookie createCookie(String name, String value, long maxAgeSeconds) {
		return ResponseCookie.from(name, value)
				.httpOnly(true)
				.secure(true)
				.path("/")
				.maxAge(maxAgeSeconds)
				.sameSite("None")
				.build();
	}

	@PostMapping("/register")
	public RegisterResponse register(@Valid @RequestBody RegisterRequest request) {
		return authService.register(request);
	}

	@PostMapping("/login")
	public ResponseEntity<?> login(@Valid @RequestBody LoginRequest request) {
		LoginResponse response = authService.login(request);
		ResponseCookie accessCookie = createCookie("accessToken", response.getAccessToken(), 15 * 60);
		ResponseCookie refreshCookie = createCookie("refreshToken", response.getRefreshToken(), 7 * 24 * 60 * 60);

		return ResponseEntity.ok()
				.header(HttpHeaders.SET_COOKIE, accessCookie.toString())
				.header(HttpHeaders.SET_COOKIE, refreshCookie.toString())
				.body("Logged in successfully");
	}

	@GetMapping("/session")
	public ResponseEntity<SessionResponse> session(
			@CookieValue(name = "refreshToken", required = false) String refreshToken) {
		if (refreshToken == null || refreshToken.isBlank()) {
			return ResponseEntity.ok(new SessionResponse(false));
		}

		try {
			var verifiedToken = refreshTokenService.verifyRefreshToken(refreshToken);
			String newAccessToken = jwtService.generateAccessToken(verifiedToken.getUser().getEmail());
			ResponseCookie accessCookie = createCookie("accessToken", newAccessToken, 15 * 60);

			return ResponseEntity.ok()
					.header(HttpHeaders.SET_COOKIE, accessCookie.toString())
					.body(new SessionResponse(true));
		} catch (RuntimeException ex) {
			ResponseCookie clearAccess = createCookie("accessToken", "", 0);
			ResponseCookie clearRefresh = createCookie("refreshToken", "", 0);

			return ResponseEntity.ok()
					.header(HttpHeaders.SET_COOKIE, clearAccess.toString())
					.header(HttpHeaders.SET_COOKIE, clearRefresh.toString())
					.body(new SessionResponse(false));
		}
	}

	@PostMapping("/refresh")
	public ResponseEntity<?> refreshToken(
			@CookieValue(name = "refreshToken", required = false) String refreshToken) {
		if (refreshToken == null || refreshToken.isBlank()) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
		}
		var verifiedToken = refreshTokenService.verifyRefreshToken(refreshToken);
		String newAccessToken = jwtService.generateAccessToken(verifiedToken.getUser().getEmail());
		ResponseCookie accessCookie = createCookie("accessToken", newAccessToken, 15 * 60);

		return ResponseEntity.ok()
				.header(HttpHeaders.SET_COOKIE, accessCookie.toString())
				.body("Refreshed token successfully");
	}

	@PostMapping("/logout")
	public ResponseEntity<?> logout(
			@CookieValue(name = "refreshToken", required = false) String refreshToken) {
		if (refreshToken != null && !refreshToken.isBlank()) {
			refreshTokenService.deleteByToken(refreshToken);
		}
		ResponseCookie clearAccess = createCookie("accessToken", "", 0);
		ResponseCookie clearRefresh = createCookie("refreshToken", "", 0);

		return ResponseEntity.ok()
				.header(HttpHeaders.SET_COOKIE, clearAccess.toString())
				.header(HttpHeaders.SET_COOKIE, clearRefresh.toString())
				.body("Logged out successfully");
	}

}
