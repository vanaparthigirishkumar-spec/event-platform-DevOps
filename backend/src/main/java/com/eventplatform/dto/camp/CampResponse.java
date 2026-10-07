package com.eventplatform.dto.camp;

import com.eventplatform.model.Camp;
import com.eventplatform.model.User;
import com.eventplatform.model.enums.CampType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CampResponse {

    private String id;
    private String title;
    private String description;
    private CampType type;
    private Instant startDate;
    private Instant endDate;
    private String location;
    private String ageGroup;
    private int capacity;
    private int availableSpots;
    private double price;
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

    public static CampResponse from(Camp camp) {
        OrganizerInfo organizerInfo = null;
        if (camp.getOrganizer() != null) {
            organizerInfo = OrganizerInfo.builder()
                    .id(camp.getOrganizer().getId())
                    .name(camp.getOrganizer().getName())
                    .email(camp.getOrganizer().getEmail())
                    .build();
        }

        return CampResponse.builder()
                .id(camp.getId())
                .title(camp.getTitle())
                .description(camp.getDescription())
                .type(camp.getType())
                .startDate(camp.getStartDate())
                .endDate(camp.getEndDate())
                .location(camp.getLocation())
                .ageGroup(camp.getAgeGroup())
                .capacity(camp.getCapacity())
                .availableSpots(camp.getAvailableSpots())
                .price(camp.getPrice())
                .organizer(organizerInfo)
                .published(camp.isPublished())
                .createdAt(camp.getCreatedAt())
                .updatedAt(camp.getUpdatedAt())
                .build();
    }

}