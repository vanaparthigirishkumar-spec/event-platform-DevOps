package com.eventplatform.controller;

import com.eventplatform.dto.common.PageResponse;
import com.eventplatform.dto.event.EventCreateRequest;
import com.eventplatform.dto.event.EventResponse;
import com.eventplatform.dto.event.EventUpdateRequest;
import com.eventplatform.model.enums.EventCategory;
import com.eventplatform.service.EventService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/events")
@RequiredArgsConstructor
public class EventController {

    private final EventService eventService;

    @PostMapping
    public ResponseEntity<EventResponse> create(@Valid @RequestBody EventCreateRequest request,
                                                 Authentication authentication) {
        EventResponse response = eventService.create(request, authentication);
        return ResponseEntity.status(201).body(response);
    }

    @GetMapping
    public ResponseEntity<PageResponse<EventResponse>> list(
            @RequestParam(defaultValue = "0") @Min(0) int page,
            @RequestParam(defaultValue = "10") @Min(1) @Max(50) int size,
            @RequestParam(defaultValue = "startDate") String sortBy,
            @RequestParam(defaultValue = "asc") String sortOrder,
            @RequestParam(required = false) EventCategory category,
            @RequestParam(required = false) String search,
            @RequestParam(required = false) Boolean published) {
        PageResponse<EventResponse> response = eventService.list(page, size, sortBy, sortOrder, category, search, published);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/my")
    public ResponseEntity<PageResponse<EventResponse>> getMyEvents(
            @RequestParam(defaultValue = "0") @Min(0) int page,
            @RequestParam(defaultValue = "10") @Min(1) @Max(50) int size,
            Authentication authentication) {
        PageResponse<EventResponse> response = eventService.getMyEvents(page, size, authentication);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<EventResponse> getById(@PathVariable String id) {
        EventResponse response = eventService.getById(id);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<EventResponse> update(@PathVariable String id,
                                                 @Valid @RequestBody EventUpdateRequest request,
                                                 Authentication authentication) {
        EventResponse response = eventService.update(id, request, authentication);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable String id, Authentication authentication) {
        eventService.delete(id, authentication);
        return ResponseEntity.noContent().build();
    }

}