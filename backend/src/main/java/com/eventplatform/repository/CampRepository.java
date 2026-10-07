package com.eventplatform.repository;

import com.eventplatform.model.Camp;
import com.eventplatform.model.User;
import com.eventplatform.model.enums.CampType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;

@Repository
public interface CampRepository extends MongoRepository<Camp, String> {

    Page<Camp> findByOrganizer(User organizer, Pageable pageable);

    Page<Camp> findByPublishedTrue(Pageable pageable);

    Page<Camp> findByTypeAndPublishedTrue(CampType type, Pageable pageable);

    Page<Camp> findByPublishedTrueAndTitleContainingIgnoreCase(String search, Pageable pageable);

    Page<Camp> findByPublishedTrueAndDescriptionContainingIgnoreCase(String search, Pageable pageable);

    @Query("{ 'published': true, 'startDate': { $gte: ?0 } }")
    Page<Camp> findUpcomingCamps(Instant fromDate, Pageable pageable);

    List<Camp> findByOrganizerAndPublishedTrue(User organizer);

    boolean existsByIdAndOrganizer(String id, User organizer);

}