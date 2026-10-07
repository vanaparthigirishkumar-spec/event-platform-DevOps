package com.eventplatform.repository;

import com.eventplatform.model.Event;
import com.eventplatform.model.User;
import com.eventplatform.model.enums.EventCategory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;

@Repository
public interface EventRepository extends MongoRepository<Event, String> {

    Page<Event> findByOrganizer(User organizer, Pageable pageable);

    Page<Event> findByPublishedTrue(Pageable pageable);

    Page<Event> findByCategoryAndPublishedTrue(EventCategory category, Pageable pageable);

    Page<Event> findByPublishedTrueAndTitleContainingIgnoreCase(String search, Pageable pageable);

    Page<Event> findByPublishedTrueAndDescriptionContainingIgnoreCase(String search, Pageable pageable);

    @Query("{ 'published': true, 'startDate': { $gte: ?0 } }")
    Page<Event> findUpcomingEvents(Instant fromDate, Pageable pageable);

    List<Event> findByOrganizerAndPublishedTrue(User organizer);

    boolean existsByIdAndOrganizer(String id, User organizer);

}