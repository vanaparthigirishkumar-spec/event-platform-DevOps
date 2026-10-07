package com.eventplatform.service;

import com.eventplatform.dto.common.PageResponse;
import com.eventplatform.dto.event.EventCreateRequest;
import com.eventplatform.dto.event.EventResponse;
import com.eventplatform.dto.event.EventUpdateRequest;
import com.eventplatform.exception.ApiException;
import com.eventplatform.model.Event;
import com.eventplatform.model.User;
import com.eventplatform.model.enums.EventCategory;
import com.eventplatform.repository.EventRepository;
import com.eventplatform.security.CustomUserDetails;
import com.eventplatform.security.SecurityService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class EventService {

    private final EventRepository eventRepository;
    private final SecurityService securityService;

    public EventResponse create(EventCreateRequest request, Authentication authentication) {
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        User organizer = userDetails.getUser();

        Event event = Event.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .category(request.getCategory())
                .startDate(request.getStartDate())
                .endDate(request.getEndDate())
                .location(request.getLocation())
                .capacity(request.getCapacity())
                .organizer(organizer)
                .published(request.getPublished() != null && request.getPublished())
                .createdAt(Instant.now())
                .updatedAt(Instant.now())
                .build();

        event = eventRepository.save(event);
        log.info("Event created: {} by user: {}", event.getId(), organizer.getEmail());
        return EventResponse.from(event);
    }

    public PageResponse<EventResponse> list(int page, int size, String sortBy, String sortOrder,
                                             EventCategory category, String search, Boolean published) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(
                sortOrder.equalsIgnoreCase("desc") ? Sort.Direction.DESC : Sort.Direction.ASC,
                sortBy
        ));

        Page<Event> eventsPage;

        if (category != null && published != null && published) {
            eventsPage = eventRepository.findByCategoryAndPublishedTrue(category, pageable);
        } else if (published != null && published) {
            if (search != null && !search.isBlank()) {
                eventsPage = eventRepository.findByPublishedTrueAndTitleContainingIgnoreCase(search, pageable);
            } else {
                eventsPage = eventRepository.findByPublishedTrue(pageable);
            }
        } else if (category != null) {
            eventsPage = eventRepository.findByCategoryAndPublishedTrue(category, pageable);
        } else {
            eventsPage = eventRepository.findAll(pageable);
        }

        List<EventResponse> content = eventsPage.getContent().stream()
                .map(EventResponse::from)
                .toList();

        return PageResponse.<EventResponse>builder()
                .content(content)
                .page(eventsPage.getNumber())
                .size(eventsPage.getSize())
                .totalElements(eventsPage.getTotalElements())
                .totalPages(eventsPage.getTotalPages())
                .first(eventsPage.isFirst())
                .last(eventsPage.isLast())
                .build();
    }

    public EventResponse getById(String id) {
        Event event = eventRepository.findById(id)
                .orElseThrow(() -> ApiException.notFound("Event not found"));
        return EventResponse.from(event);
    }

    public EventResponse update(String id, EventUpdateRequest request, Authentication authentication) {
        if (!securityService.canModifyEvent(id, authentication)) {
            throw ApiException.forbidden("You do not have permission to update this event");
        }

        Event event = eventRepository.findById(id)
                .orElseThrow(() -> ApiException.notFound("Event not found"));

        if (request.getTitle() != null) event.setTitle(request.getTitle());
        if (request.getDescription() != null) event.setDescription(request.getDescription());
        if (request.getCategory() != null) event.setCategory(request.getCategory());
        if (request.getStartDate() != null) event.setStartDate(request.getStartDate());
        if (request.getEndDate() != null) event.setEndDate(request.getEndDate());
        if (request.getLocation() != null) event.setLocation(request.getLocation());
        if (request.getCapacity() != null) event.setCapacity(request.getCapacity());
        if (request.getPublished() != null) event.setPublished(request.getPublished());

        event.setUpdatedAt(Instant.now());
        event = eventRepository.save(event);

        log.info("Event updated: {} by user: {}", id, ((CustomUserDetails) authentication.getPrincipal()).getUser().getEmail());
        return EventResponse.from(event);
    }

    public void delete(String id, Authentication authentication) {
        if (!securityService.canModifyEvent(id, authentication)) {
            throw ApiException.forbidden("You do not have permission to delete this event");
        }

        if (!eventRepository.existsById(id)) {
            throw ApiException.notFound("Event not found");
        }

        eventRepository.deleteById(id);
        log.info("Event deleted: {} by user: {}", id, ((CustomUserDetails) authentication.getPrincipal()).getUser().getEmail());
    }

    public PageResponse<EventResponse> getMyEvents(int page, int size, Authentication authentication) {
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        User user = userDetails.getUser();

        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<Event> eventsPage = eventRepository.findByOrganizer(user, pageable);

        List<EventResponse> content = eventsPage.getContent().stream()
                .map(EventResponse::from)
                .toList();

        return PageResponse.<EventResponse>builder()
                .content(content)
                .page(eventsPage.getNumber())
                .size(eventsPage.getSize())
                .totalElements(eventsPage.getTotalElements())
                .totalPages(eventsPage.getTotalPages())
                .first(eventsPage.isFirst())
                .last(eventsPage.isLast())
                .build();
    }

}