package com.eventplatform.model;

import com.eventplatform.model.enums.CampType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "camps")
public class Camp {

    @Id
    private String id;

    private String title;

    private String description;

    private CampType type;

    private Instant startDate;

    private Instant endDate;

    private String location;

    private String ageGroup;

    private int capacity;

    private double price;

    @DBRef
    @Indexed
    private User organizer;

    @DBRef
    @Builder.Default
    private List<User> attendees = new ArrayList<>();

    private boolean published;

    private Instant createdAt;

    private Instant updatedAt;

    public int getAvailableSpots() {
        return capacity - attendees.size();
    }

}