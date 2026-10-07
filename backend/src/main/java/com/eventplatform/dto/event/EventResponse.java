package com.eventplatform.dto.event;

import com.eventplatform.model.Event;
import com.eventplatform.model.User;
import com.eventplatform.model.enums.EventCategory;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EventResponse {

    private String id;
    private String title;
    private String description;
    private EventCategory category;
    private Instant startDate;
    private Instant endDate;
    private String location;
    private int capacity;
    private int availableSpots;
    private OrganizerInfo organizer;
    private boolean published;
    private Instant createdAt;
    private Instant updatedAt;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class OrganizerInfo {
        private String id;
        private String name;
        private String email;
    }

    public static EventResponse from(Event event) {
        OrganizerInfo organizerInfo = null;
        if (event.getOrganizer() != null) {
            organizerInfo = OrganizerInfo.builder()
                    .id(event.getOrganizer().getId())
                    .name(event.getOrganizer().getName())
                    .email(event.getOrganizer().getEmail())
                    .build();
        }

        return EventResponse.builder()
                .id(event.getId())
                .title(event.getTitle())
                .description(event.getDescription())
                .category(event.getCategory())
                .startDate(event.getStartDate())
                .endDate(event.getEndDate())
                .location(event.getLocation())
                .capacity(event.getCapacity())
                .availableSpots(event.getAvailableSpots())
                .organizer(organizerInfo)
                .published(event.isPublished())
                .createdAt(event.getCreatedAt())
                .updatedAt(event.getUpdatedAt())
                .build();
    }

}