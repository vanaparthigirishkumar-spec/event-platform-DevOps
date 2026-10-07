package com.eventplatform.dto.camp;

import com.eventplatform.model.enums.CampType;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CampCreateRequest {

    @NotBlank(message = "Title is required")
    @Size(min = 3, max = 200, message = "Title must be between 3 and 200 characters")
    private String title;

    @NotBlank(message = "Description is required")
    @Size(min = 10, max = 5000, message = "Description must be between 10 and 5000 characters")
    private String description;

    @NotNull(message = "Type is required")
    private CampType type;

    @NotNull(message = "Start date is required")
    @Future(message = "Start date must be in the future")
    private Instant startDate;

    @NotNull(message = "End date is required")
    @Future(message = "End date must be in the future")
    private Instant endDate;

    @NotBlank(message = "Location is required")
    @Size(min = 3, max = 500, message = "Location must be between 3 and 500 characters")
    private String location;

    @NotBlank(message = "Age group is required")
    @Size(min = 2, max = 50, message = "Age group must be between 2 and 50 characters")
    private String ageGroup;

    @NotNull(message = "Capacity is required")
    @Positive(message = "Capacity must be positive")
    private Integer capacity;

    @NotNull(message = "Price is required")
    @PositiveOrZero(message = "Price cannot be negative")
    private Double price;

    private Boolean published;

}