package com.naru.service;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.naru.dto.ProfileResponse;
import com.naru.dto.UpdateProfileRequest;
import com.naru.entity.User;
import com.naru.exception.ResourceNotFoundException;
import com.naru.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ProfileService {

	private final UserRepository userRepository;
	private final StorageService storageService;

	@Transactional(readOnly = true)
	public ProfileResponse getProfile(Authentication authentication) {
		return toResponse(requireUser(authentication));
	}

	@Transactional
	public ProfileResponse updateProfile(UpdateProfileRequest request, Authentication authentication) {
		User user = requireUser(authentication);
		user.setOrganizationName(blankToNull(request.getOrganizationName()));
		user.setBio(blankToNull(request.getBio()));

		String oldLogoUrl = user.getLogoUrl();
		String newLogoUrl = blankToNull(request.getLogoUrl());
		if (oldLogoUrl != null && !oldLogoUrl.isBlank() && !oldLogoUrl.equals(newLogoUrl)) {
			storageService.deleteImage(oldLogoUrl);
		}
		user.setLogoUrl(newLogoUrl);
		user.setWebsite(blankToNull(request.getWebsite()));
		return toResponse(user);
	}

	private User requireUser(Authentication authentication) {
		return userRepository.findByEmail(authentication.getName())
				.orElseThrow(() -> new ResourceNotFoundException("User not found"));
	}

	private ProfileResponse toResponse(User user) {
		return ProfileResponse.builder()
				.id(user.getId())
				.email(user.getEmail())
				.organizationName(user.getOrganizationName())
				.bio(user.getBio())
				.logoUrl(user.getLogoUrl())
				.website(user.getWebsite())
				.build();
	}

	private String blankToNull(String value) {
		if (value == null || value.isBlank()) {
			return null;
		}
		return value.trim();
	}
}
