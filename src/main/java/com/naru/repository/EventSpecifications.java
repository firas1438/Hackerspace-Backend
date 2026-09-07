package com.naru.repository;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Locale;

import org.springframework.data.jpa.domain.Specification;

import com.naru.entity.Event;
import com.naru.enums.EventFormat;
import com.naru.enums.EventStatus;
import com.naru.enums.EventType;

import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import jakarta.persistence.criteria.Subquery;

public final class EventSpecifications {

	private EventSpecifications() {
	}

	public static Specification<Event> withFilters(
			String query,
			Collection<EventType> types,
			Collection<EventFormat> formats,
			Collection<String> cities,
			Collection<EventStatus> statuses,
			Collection<String> tags) {

		return (root, criteriaQuery, cb) -> {
			List<Predicate> predicates = new ArrayList<>();

			if (query != null && !query.isBlank()) {
				String like = "%" + query.trim().toLowerCase(Locale.ROOT) + "%";
				predicates.add(cb.or(
						cb.like(cb.lower(root.get("title")), like),
						cb.like(cb.lower(root.get("description")), like),
						cb.like(cb.lower(root.get("location")), like),
						cb.like(cb.lower(root.get("city")), like)));
			}

			if (types != null && !types.isEmpty()) {
				predicates.add(root.get("type").in(types));
			}

			if (formats != null && !formats.isEmpty()) {
				predicates.add(root.get("format").in(formats));
			}

			if (cities != null && !cities.isEmpty()) {
				predicates.add(cb.lower(root.get("city")).in(
						cities.stream().map(city -> city.toLowerCase(Locale.ROOT)).toList()));
			}

			if (tags != null && !tags.isEmpty() && criteriaQuery != null) {
				List<String> normalizedTags = tags.stream()
						.map(tag -> tag.toLowerCase(Locale.ROOT).replaceFirst("^#", "").trim())
						.filter(tag -> !tag.isBlank())
						.toList();

				if (!normalizedTags.isEmpty()) {
					Subquery<Long> tagSubquery = criteriaQuery.subquery(Long.class);
					Root<Event> tagRoot = tagSubquery.from(Event.class);
					tagSubquery.select(tagRoot.get("id"));
					tagSubquery.where(cb.lower(tagRoot.join("tags")).in(normalizedTags));
					predicates.add(root.get("id").in(tagSubquery));
				}
			}

			if (statuses != null && !statuses.isEmpty()) {
				Instant now = Instant.now();
				List<Predicate> statusPredicates = new ArrayList<>();

				for (EventStatus status : statuses) {
					statusPredicates.add(statusPredicate(root, cb, status, now));
				}

				predicates.add(cb.or(statusPredicates.toArray(Predicate[]::new)));
			}

			if (predicates.isEmpty()) {
				return cb.conjunction();
			}

			return cb.and(predicates.toArray(Predicate[]::new));
		};
	}

	private static Predicate statusPredicate(
			Root<Event> root,
			jakarta.persistence.criteria.CriteriaBuilder cb,
			EventStatus status,
			Instant now) {
		var endDate = root.<Instant>get("endDate");
		var startDate = root.<Instant>get("startDate");
		var effectiveEnd = cb.coalesce(endDate, startDate);

		return switch (status) {
			case UPCOMING -> cb.greaterThan(startDate, now);
			case ONGOING -> cb.and(
					cb.lessThanOrEqualTo(startDate, now),
					cb.greaterThanOrEqualTo(effectiveEnd, now));
			case PAST -> cb.lessThan(effectiveEnd, now);
		};
	}
}
