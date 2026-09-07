package com.naru.entity;

import java.time.Instant;
import java.util.HashSet;
import java.util.Set;

import com.naru.enums.EventFormat;
import com.naru.enums.EventType;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import org.hibernate.annotations.BatchSize;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "events")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Event {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false)
	private String title;

	@Column(nullable = false, length = 500)
	private String summary;

	@Column(nullable = false, columnDefinition = "TEXT")
	private String description;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private EventType type;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private EventFormat format;

	@Column(nullable = false)
	private String location;

	@Column(nullable = false)
	private String city;

	@Column(nullable = false)
	private Instant startDate;

	private Instant endDate;

	private String imageUrl;

	@Column(length = 500)
	private String registrationUrl;

	@ElementCollection(fetch = FetchType.LAZY)
	@CollectionTable(name = "event_tags", joinColumns = @JoinColumn(name = "event_id"))
	@Column(name = "tag", nullable = false)
	@BatchSize(size = 20)
	@Builder.Default
	private Set<String> tags = new HashSet<>();

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "created_by_id")
	private User createdBy;

	@Column(nullable = false, updatable = false)
	private Instant createdAt;

	@Column(nullable = false)
	private Instant updatedAt;

	@PrePersist
	void onCreate() {
		Instant now = Instant.now();
		createdAt = now;
		updatedAt = now;
	}

	@PreUpdate
	void onUpdate() {
		updatedAt = Instant.now();
	}
}
