package com.naru.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.naru.dto.CreateEventRequest;
import com.naru.dto.EventFilterOptionsResponse;
import com.naru.dto.EventPageResponse;
import com.naru.dto.EventResponse;
import com.naru.dto.UpdateEventRequest;
import com.naru.enums.EventFormat;
import com.naru.enums.EventStatus;
import com.naru.enums.EventType;
import com.naru.service.EventService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/events")
@RequiredArgsConstructor
public class EventController {

	private final EventService eventService;

	@GetMapping
	public EventPageResponse listEvents(
			@RequestParam(required = false) String q,
			@RequestParam(required = false) List<EventType> types,
			@RequestParam(required = false) List<EventFormat> formats,
			@RequestParam(required = false) List<String> cities,
			@RequestParam(required = false) List<EventStatus> statuses,
			@RequestParam(required = false) List<String> tags,
			@RequestParam(defaultValue = "0") int page,
			@RequestParam(defaultValue = "9") int size) {
		return eventService.listEvents(q, types, formats, cities, statuses, tags, page, size);
	}

	@GetMapping("/filters")
	public EventFilterOptionsResponse getFilterOptions() {
		return eventService.getFilterOptions();
	}

	@GetMapping("/mine")
	@PreAuthorize("hasAnyRole('USER','ADMIN')")
	public EventPageResponse listMyEvents(
			Authentication authentication,
			@RequestParam(defaultValue = "0") int page,
			@RequestParam(defaultValue = "9") int size) {
		return eventService.listMyEvents(authentication, page, size);
	}

	@GetMapping("/{id}")
	public EventResponse getEvent(@PathVariable Long id) {
		return eventService.getEvent(id);
	}

	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	@PreAuthorize("hasAnyRole('USER','ADMIN')")
	public EventResponse createEvent(
			@Valid @RequestBody CreateEventRequest request,
			Authentication authentication) {
		return eventService.createEvent(request, authentication);
	}

	@PutMapping("/{id}")
	@PreAuthorize("hasAnyRole('USER','ADMIN')")
	public EventResponse updateEvent(
			@PathVariable Long id,
			@Valid @RequestBody UpdateEventRequest request,
			Authentication authentication) {
		return eventService.updateEvent(id, request, authentication);
	}

	@DeleteMapping("/{id}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	@PreAuthorize("hasAnyRole('USER','ADMIN')")
	public void deleteEvent(@PathVariable Long id, Authentication authentication) {
		eventService.deleteEvent(id, authentication);
	}
}
