package com.naru.dto;

import java.time.Instant;
import java.util.Set;

import com.naru.enums.EventFormat;
import com.naru.enums.EventStatus;
import com.naru.enums.EventType;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class EventResponse {

	private Long id;
	private String title;
	private String summary;
	private String description;
	private EventType type;
	private EventFormat format;
	private EventStatus status;
	private String location;
	private String city;
	private Instant startDate;
	private Instant endDate;
	private String imageUrl;
	private String registrationUrl;
	private Set<String> tags;
	private Long createdById;
	private String organizerName;
	private String organizerEmail;
	private String organizerLogoUrl;
	private String organizerWebsite;
	private String organizerBio;
}
