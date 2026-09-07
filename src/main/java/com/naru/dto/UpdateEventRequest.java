package com.naru.dto;

import java.time.Instant;
import java.util.Set;

import com.naru.enums.EventFormat;
import com.naru.enums.EventType;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateEventRequest {

	@NotBlank
	@Size(max = 200)
	private String title;

	@NotBlank
	@Size(max = 500)
	private String summary;

	@NotBlank
	@Size(max = 5000)
	private String description;

	@NotNull
	private EventType type;

	@NotNull
	private EventFormat format;

	@NotBlank
	@Size(max = 200)
	private String location;

	@NotBlank
	@Size(max = 120)
	private String city;

	@NotNull
	private Instant startDate;

	private Instant endDate;

	@Size(max = 500)
	private String imageUrl;

	@Size(max = 500)
	private String registrationUrl;

	private Set<@Size(max = 50) String> tags;
}
