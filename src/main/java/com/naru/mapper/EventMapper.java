package com.naru.mapper;

import java.time.Instant;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.stream.Collectors;

import com.naru.dto.CreateEventRequest;
import com.naru.dto.EventResponse;
import com.naru.dto.UpdateEventRequest;
import com.naru.entity.Event;
import com.naru.enums.EventStatus;
import org.hibernate.Hibernate;

public final class EventMapper {

	private EventMapper() {
	}

	public static EventStatus resolveStatus(Instant startDate, Instant endDate) {
		Instant now = Instant.now();
		Instant effectiveEnd = endDate != null ? endDate : startDate;

		if (startDate.isAfter(now)) {
			return EventStatus.UPCOMING;
		}
		if (effectiveEnd.isBefore(now)) {
			return EventStatus.PAST;
		}
		return EventStatus.ONGOING;
	}

	public static Set<String> normalizeTags(Set<String> tags) {
		if (tags == null) {
			return Set.of();
		}

		return tags.stream()
				.map(tag -> tag == null ? "" : tag.trim().replaceFirst("^#", ""))
				.filter(tag -> !tag.isBlank())
				.map(tag -> tag.replaceAll("\\s+", " "))
				.collect(Collectors.toCollection(LinkedHashSet::new));
	}

	public static EventResponse toResponse(Event event) {
		com.naru.entity.User createdBy = event.getCreatedBy();
		boolean isUserLoaded = createdBy != null && Hibernate.isInitialized(createdBy);

		return EventResponse.builder()
				.id(event.getId())
				.title(event.getTitle())
				.summary(event.getSummary())
				.description(event.getDescription())
				.type(event.getType())
				.format(event.getFormat())
				.status(resolveStatus(event.getStartDate(), event.getEndDate()))
				.location(event.getLocation())
				.city(event.getCity())
				.startDate(event.getStartDate())
				.endDate(event.getEndDate())
				.imageUrl(event.getImageUrl())
				.registrationUrl(event.getRegistrationUrl())
				.tags(event.getTags() == null ? Set.of() : event.getTags())
				.createdById(isUserLoaded ? createdBy.getId() : null)
				.organizerName(isUserLoaded ? (createdBy.getOrganizationName() != null && !createdBy.getOrganizationName().isBlank() ? createdBy.getOrganizationName() : createdBy.getEmail()) : null)
				.organizerEmail(isUserLoaded ? createdBy.getEmail() : null)
				.organizerLogoUrl(isUserLoaded ? createdBy.getLogoUrl() : null)
				.organizerWebsite(isUserLoaded ? createdBy.getWebsite() : null)
				.organizerBio(isUserLoaded ? createdBy.getBio() : null)
				.build();
	}

	public static void applyCreate(Event event, CreateEventRequest request) {
		event.setTitle(request.getTitle().trim());
		event.setSummary(request.getSummary().trim());
		event.setDescription(request.getDescription().trim());
		event.setType(request.getType());
		event.setFormat(request.getFormat());
		event.setLocation(request.getLocation().trim());
		event.setCity(request.getCity().trim());
		event.setStartDate(request.getStartDate());
		event.setEndDate(request.getEndDate());
		event.setImageUrl(blankToNull(request.getImageUrl()));
		event.setRegistrationUrl(blankToNull(request.getRegistrationUrl()));
		event.setTags(normalizeTags(request.getTags()));
	}

	public static void applyUpdate(Event event, UpdateEventRequest request) {
		event.setTitle(request.getTitle().trim());
		event.setSummary(request.getSummary().trim());
		event.setDescription(request.getDescription().trim());
		event.setType(request.getType());
		event.setFormat(request.getFormat());
		event.setLocation(request.getLocation().trim());
		event.setCity(request.getCity().trim());
		event.setStartDate(request.getStartDate());
		event.setEndDate(request.getEndDate());
		event.setImageUrl(blankToNull(request.getImageUrl()));
		event.setRegistrationUrl(blankToNull(request.getRegistrationUrl()));
		event.setTags(normalizeTags(request.getTags()));
	}

	private static Long createdById(Event event) {
		if (event.getCreatedBy() == null || !Hibernate.isInitialized(event.getCreatedBy())) {
			return null;
		}
		return event.getCreatedBy().getId();
	}

	private static String blankToNull(String value) {
		if (value == null || value.isBlank()) {
			return null;
		}
		return value.trim();
	}
}

