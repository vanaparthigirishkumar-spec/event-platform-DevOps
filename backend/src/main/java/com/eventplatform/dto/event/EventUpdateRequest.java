package com.eventplatform.dto.event;

import com.eventplatform.model.enums.EventCategory;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EventUpdateRequest {

    @Size(min = 3, max = 200, message = "Title must be between 3 and 200 characters")
    private String title;

    @Size(min = 10, max = 5000, message = "Description must be between 10 and 5000 characters")
    private String description;

    private EventCategory category;

    @Future(message = "Start date must be in the future")
    private Instant startDate;

    @Future(message = "End date must be in the future")
    private Instant endDate;

    @Size(min = 3, max = 500, message = "Location must be between 3 and 500 characters")
    private String location;

    @Positive(message = "Capacity must be positive")
    private Integer capacity;

    private Boolean published;

}