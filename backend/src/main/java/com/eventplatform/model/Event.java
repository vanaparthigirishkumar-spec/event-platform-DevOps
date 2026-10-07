package com.eventplatform.model;

import com.eventplatform.model.enums.EventCategory;
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
@Document(collection = "events")
public class Event {

    @Id
    private String id;

    private String title;

    private String description;

    private EventCategory category;

    private Instant startDate;

    private Instant endDate;

    private String location;

    private int capacity;

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