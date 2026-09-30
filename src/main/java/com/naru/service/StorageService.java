package com.naru.service;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import software.amazon.awssdk.core.ResponseBytes;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class StorageService {

	private static final long MAX_FILE_SIZE = 1 * 1024 * 1024; // 1 MB
	private static final List<String> ALLOWED_CONTENT_TYPES = Arrays.asList(
			"image/jpeg",
			"image/jpg",
			"image/png",
			"image/webp"
	);
	private static final List<String> ALLOWED_EXTENSIONS = Arrays.asList(
			"jpg", "jpeg", "png", "webp"
	);

	private final S3Client s3Client;

	@Value("${aws.s3.bucket}")
	private String bucketName;

	@Value("${aws.s3.endpoint-url}")
	private String endpointUrl;

	public String uploadImage(MultipartFile file) {
		if (file == null || file.isEmpty()) {
			throw new IllegalArgumentException("File must not be empty");
		}

		if (file.getSize() > MAX_FILE_SIZE) {
			throw new IllegalArgumentException("File size exceeds maximum allowed limit of 1 MB");
		}

		String contentType = file.getContentType();
		if (contentType == null || !ALLOWED_CONTENT_TYPES.contains(contentType.toLowerCase())) {
			throw new IllegalArgumentException("Invalid image format. Allowed formats: JPEG, PNG, WEBP");
		}

		String originalFilename = file.getOriginalFilename();
		String extension = getFileExtension(originalFilename);
		if (extension == null || !ALLOWED_EXTENSIONS.contains(extension.toLowerCase())) {
			throw new IllegalArgumentException("Invalid file extension. Allowed extensions: .jpg, .jpeg, .png, .webp");
		}

		String key = "uploads/" + UUID.randomUUID() + "." + extension.toLowerCase();

		try (InputStream inputStream = file.getInputStream()) {
			PutObjectRequest putObjectRequest = PutObjectRequest.builder()
					.bucket(bucketName)
					.key(key)
					.contentType(contentType)
					.build();

			s3Client.putObject(putObjectRequest, RequestBody.fromInputStream(inputStream, file.getSize()));

			// Return backend proxy URL so images load reliably across all environments without S3 ACL/CORS restrictions
			return "http://localhost:8080/upload/files/" + key;
		} catch (IOException e) {
			throw new RuntimeException("Failed to upload image to Object Storage: " + e.getMessage(), e);
		}
	}

	public void deleteImage(String imageUrl) {
		if (imageUrl == null || imageUrl.isBlank()) {
			return;
		}

		int uploadsIndex = imageUrl.indexOf("uploads/");
		if (uploadsIndex == -1) {
			return;
		}

		String key = imageUrl.substring(uploadsIndex);

		try {
			DeleteObjectRequest deleteObjectRequest = DeleteObjectRequest.builder()
					.bucket(bucketName)
					.key(key)
					.build();
			s3Client.deleteObject(deleteObjectRequest);
		} catch (Exception e) {
			// Log error if needed
		}
	}

	public ResponseBytes<GetObjectResponse> getFile(String key) {
		GetObjectRequest getObjectRequest = GetObjectRequest.builder()
				.bucket(bucketName)
				.key(key)
				.build();
		return s3Client.getObjectAsBytes(getObjectRequest);
	}

	private String getFileExtension(String filename) {
		if (filename == null || !filename.contains(".")) {
			return null;
		}
		return filename.substring(filename.lastIndexOf(".") + 1);
	}
}
