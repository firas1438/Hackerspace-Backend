package com.naru.controller;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.HandlerMapping;

import com.naru.dto.UploadResponse;
import com.naru.service.StorageService;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import software.amazon.awssdk.core.ResponseBytes;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;

@RestController
@RequestMapping("/upload")
@RequiredArgsConstructor
public class UploadController {

	private final StorageService storageService;

	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	@PreAuthorize("hasAnyRole('USER','ADMIN')")
	public UploadResponse uploadImage(@RequestParam("file") MultipartFile file) {
		String imageUrl = storageService.uploadImage(file);
		return new UploadResponse(imageUrl);
	}

	@DeleteMapping
	@ResponseStatus(HttpStatus.NO_CONTENT)
	@PreAuthorize("hasAnyRole('USER','ADMIN')")
	public void deleteImage(@RequestParam("url") String url) {
		storageService.deleteImage(url);
	}

	@GetMapping("/files/**")
	public ResponseEntity<byte[]> getFile(HttpServletRequest request) {
		String path = (String) request.getAttribute(HandlerMapping.PATH_WITHIN_HANDLER_MAPPING_ATTRIBUTE);
		String bestMatchPattern = (String) request.getAttribute(HandlerMapping.BEST_MATCHING_PATTERN_ATTRIBUTE);
		AntPathMatcher apm = new AntPathMatcher();
		String key = apm.extractPathWithinPattern(bestMatchPattern, path);

		ResponseBytes<GetObjectResponse> objectBytes = storageService.getFile(key);
		byte[] data = objectBytes.asByteArray();
		String contentType = objectBytes.response().contentType();

		return ResponseEntity.ok()
				.header(HttpHeaders.CONTENT_TYPE, contentType != null ? contentType : MediaType.IMAGE_JPEG_VALUE)
				.header(HttpHeaders.CACHE_CONTROL, "public, max-age=31536000, immutable")
				.body(data);
	}
}
