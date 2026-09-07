package com.naru.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.naru.dto.ProfileResponse;
import com.naru.dto.UpdateProfileRequest;
import com.naru.service.ProfileService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/profile")
@RequiredArgsConstructor
public class ProfileController {

	private final ProfileService profileService;

	@GetMapping
	@PreAuthorize("hasAnyRole('USER','ADMIN')")
	public ProfileResponse getProfile(Authentication authentication) {
		return profileService.getProfile(authentication);
	}

	@PutMapping
	@PreAuthorize("hasAnyRole('USER','ADMIN')")
	public ProfileResponse updateProfile(
			@Valid @RequestBody UpdateProfileRequest request,
			Authentication authentication) {
		return profileService.updateProfile(request, authentication);
	}
}
