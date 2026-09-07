package com.naru.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import com.naru.entity.Event;

public interface EventRepository extends JpaRepository<Event, Long>, JpaSpecificationExecutor<Event> {

	@EntityGraph(attributePaths = { "tags", "createdBy" })
	Optional<Event> findWithDetailsById(Long id);

	@EntityGraph(attributePaths = { "tags" })
	Page<Event> findByCreatedById(Long createdById, Pageable pageable);

	@Query("SELECT DISTINCT e.city FROM Event e WHERE e.city IS NOT NULL ORDER BY e.city")
	List<String> findDistinctCities();

	@Query("SELECT DISTINCT t FROM Event e JOIN e.tags t ORDER BY t")
	List<String> findDistinctTags();
}
