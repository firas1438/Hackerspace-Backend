package com.naru.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.naru.dto.CreateEventRequest;
import com.naru.dto.EventFilterOptionsResponse;
import com.naru.dto.EventPageResponse;
import com.naru.dto.EventResponse;
import com.naru.dto.UpdateEventRequest;
import com.naru.entity.Event;
import com.naru.entity.User;
import com.naru.enums.City;
import com.naru.enums.EventFormat;
import com.naru.enums.EventStatus;
import com.naru.enums.EventType;
import com.naru.enums.Role;
import com.naru.exception.ResourceNotFoundException;
import com.naru.mapper.EventMapper;
import com.naru.repository.EventRepository;
import com.naru.repository.EventSpecifications;
import com.naru.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class EventService {

	public static final int DEFAULT_PAGE_SIZE = 9;

	private final EventRepository eventRepository;
	private final UserRepository userRepository;
	private final StorageService storageService;

	@Transactional(readOnly = true)
	public EventPageResponse listEvents(
			String query,
			List<EventType> types,
			List<EventFormat> formats,
			List<String> cities,
			List<EventStatus> statuses,
			List<String> tags,
			int page,
			int size) {
		int pageIndex = Math.max(page, 0);
		int pageSize = size > 0 ? Math.min(size, 50) : DEFAULT_PAGE_SIZE;

		Page<Event> result = eventRepository.findAll(
				EventSpecifications.withFilters(query, types, formats, cities, statuses, tags),
				PageRequest.of(pageIndex, pageSize, Sort.by(Sort.Direction.ASC, "startDate")));

		return EventPageResponse.builder()
				.content(result.getContent().stream().map(EventMapper::toResponse).toList())
				.page(result.getNumber())
				.size(result.getSize())
				.totalElements(result.getTotalElements())
				.totalPages(result.getTotalPages())
				.build();
	}

	@Transactional(readOnly = true)
	public EventPageResponse listMyEvents(Authentication authentication, int page, int size) {
		User creator = userRepository.findByEmail(authentication.getName())
				.orElseThrow(() -> new ResourceNotFoundException("User not found"));

		int pageIndex = Math.max(page, 0);
		int pageSize = size > 0 ? Math.min(size, 50) : DEFAULT_PAGE_SIZE;

		Page<Event> result = eventRepository.findByCreatedById(
				creator.getId(),
				PageRequest.of(pageIndex, pageSize, Sort.by(Sort.Direction.DESC, "startDate")));

		return EventPageResponse.builder()
				.content(result.getContent().stream().map(EventMapper::toResponse).toList())
				.page(result.getNumber())
				.size(result.getSize())
				.totalElements(result.getTotalElements())
				.totalPages(result.getTotalPages())
				.build();
	}

	@Transactional(readOnly = true)
	public EventResponse getEvent(Long id) {
		Event event = eventRepository.findWithDetailsById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Event not found"));
		return EventMapper.toResponse(event);
	}

	@Transactional(readOnly = true)
	public EventFilterOptionsResponse getFilterOptions() {
		return EventFilterOptionsResponse.builder()
				.cities(eventRepository.findDistinctCities())
				.tags(eventRepository.findDistinctTags())
				.build();
	}

	@Transactional
	public EventResponse createEvent(CreateEventRequest request, Authentication authentication) {
		validateCity(request.getCity());
		validateDates(request.getStartDate(), request.getEndDate());

		User creator = userRepository.findByEmail(authentication.getName())
				.orElseThrow(() -> new ResourceNotFoundException("User not found"));

		Event event = new Event();
		EventMapper.applyCreate(event, request);
		event.setCreatedBy(creator);

		return EventMapper.toResponse(eventRepository.save(event));
	}

	@Transactional
	public EventResponse updateEvent(Long id, UpdateEventRequest request, Authentication authentication) {
		validateCity(request.getCity());
		validateDates(request.getStartDate(), request.getEndDate());

		Event event = eventRepository.findWithDetailsById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Event not found"));

		assertCanMutate(event, authentication);
		assertCanEdit(event);

		String oldImageUrl = event.getImageUrl();
		EventMapper.applyUpdate(event, request);
		String newImageUrl = event.getImageUrl();

		if (oldImageUrl != null && !oldImageUrl.isBlank() && !oldImageUrl.equals(newImageUrl)) {
			storageService.deleteImage(oldImageUrl);
		}

		return EventMapper.toResponse(event);
	}

	@Transactional
	public void deleteEvent(Long id, Authentication authentication) {
		Event event = eventRepository.findWithDetailsById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Event not found"));

		assertCanMutate(event, authentication);
		String imageUrl = event.getImageUrl();
		eventRepository.delete(event);
		if (imageUrl != null && !imageUrl.isBlank()) {
			storageService.deleteImage(imageUrl);
		}
	}

	private void assertCanEdit(Event event) {
		if (EventMapper.resolveStatus(event.getStartDate(), event.getEndDate()) == EventStatus.PAST) {
			throw new IllegalArgumentException("Past events cannot be modified");
		}
	}

	private void assertCanMutate(Event event, Authentication authentication) {
		User currentUser = userRepository.findByEmail(authentication.getName())
				.orElseThrow(() -> new ResourceNotFoundException("User not found"));

		boolean isOwner = event.getCreatedBy() != null
				&& event.getCreatedBy().getId().equals(currentUser.getId());
		boolean isAdmin = currentUser.getRole() == Role.ROLE_ADMIN;

		if (!isOwner && !isAdmin) {
			throw new AccessDeniedException("You cannot modify this event");
		}
	}

	private void validateCity(String city) {
		if (!City.isValid(city)) {
			throw new IllegalArgumentException("Invalid city: " + city);
		}
	}

	private void validateDates(java.time.Instant startDate, java.time.Instant endDate) {
		if (endDate != null && endDate.isBefore(startDate)) {
			throw new IllegalArgumentException("End date cannot be before start date");
		}
	}
}
