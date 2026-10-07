package com.eventplatform;

import com.eventplatform.dto.event.EventCreateRequest;
import com.eventplatform.model.Event;
import com.eventplatform.model.User;
import com.eventplatform.model.enums.EventCategory;
import com.eventplatform.model.enums.UserRole;
import com.eventplatform.repository.EventRepository;
import com.eventplatform.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Testcontainers
@ActiveProfiles("dev")
@Import(com.eventplatform.config.TestMongoConfig.class)
class EventControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EventRepository eventRepository;

    private String accessToken;
    private User testUser;

    @BeforeEach
    void setUp() throws Exception {
        userRepository.deleteAll();
        eventRepository.deleteAll();

        // Create and register user
        testUser = User.builder()
                .email("organizer@example.com")
                .passwordHash("hashed")
                .name("Organizer")
                .role(UserRole.USER)
                .build();
        testUser = userRepository.save(testUser);

        // Login to get token
        String loginResponse = mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "email": "organizer@example.com",
                                    "password": "password123"
                                }
                                """))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        accessToken = extractToken(loginResponse, "accessToken");
    }

    @Test
    void createEvent_ShouldCreateAndReturnEvent() throws Exception {
        mockMvc.perform(post("/api/events")
                        .header("Authorization", "Bearer " + accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "title": "Test Event",
                                    "description": "This is a test event description",
                                    "category": "TECHNOLOGY",
                                    "startDate": "2099-01-01T10:00:00Z",
                                    "endDate": "2099-01-01T12:00:00Z",
                                    "location": "Test Location",
                                    "capacity": 100,
                                    "published": true
                                }
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.title").value("Test Event"))
                .andExpect(jsonPath("$.data.organizer.id").exists());
    }

    @Test
    void listEvents_ShouldReturnPagedEvents() throws Exception {
        // Create an event first
        Event event = Event.builder()
                .title("Listed Event")
                .description("Event for listing")
                .category(EventCategory.TECHNOLOGY)
                .startDate(Instant.parse("2099-01-01T10:00:00Z"))
                .endDate(Instant.parse("2099-01-01T12:00:00Z"))
                .location("List Location")
                .capacity(50)
                .organizer(testUser)
                .published(true)
                .build();
        eventRepository.save(event);

        mockMvc.perform(get("/api/events")
                        .param("page", "0")
                        .param("size", "10")
                        .param("published", "true"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.content").isArray())
                .andExpect(jsonPath("$.data.totalElements").value(1));
    }

    @Test
    void getEvent_ShouldReturnEvent() throws Exception {
        Event event = Event.builder()
                .title("Get Event")
                .description("Event to get")
                .category(EventCategory.BUSINESS)
                .startDate(Instant.parse("2099-02-01T10:00:00Z"))
                .endDate(Instant.parse("2099-02-01T12:00:00Z"))
                .location("Get Location")
                .capacity(30)
                .organizer(testUser)
                .published(true)
                .build();
        Event saved = eventRepository.save(event);

        mockMvc.perform(get("/api/events/" + saved.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.id").value(saved.getId()));
    }

    @Test
    void updateEvent_ShouldUpdateWhenOwner() throws Exception {
        Event event = Event.builder()
                .title("Original Title")
                .description("Original")
                .category(EventCategory.TECHNOLOGY)
                .startDate(Instant.parse("2099-03-01T10:00:00Z"))
                .endDate(Instant.parse("2099-03-01T12:00:00Z"))
                .location("Original Location")
                .capacity(20)
                .organizer(testUser)
                .published(true)
                .build();
        Event saved = eventRepository.save(event);

        mockMvc.perform(put("/api/events/" + saved.getId())
                        .header("Authorization", "Bearer " + accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "title": "Updated Title"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.title").value("Updated Title"));
    }

    @Test
    void deleteEvent_ShouldDeleteWhenOwner() throws Exception {
        Event event = Event.builder()
                .title("To Delete")
                .description("Will be deleted")
                .category(EventCategory.TECHNOLOGY)
                .startDate(Instant.parse("2099-04-01T10:00:00Z"))
                .endDate(Instant.parse("2099-04-01T12:00:00Z"))
                .location("Delete Location")
                .capacity(10)
                .organizer(testUser)
                .published(true)
                .build();
        Event saved = eventRepository.save(event);

        mockMvc.perform(delete("/api/events/" + saved.getId())
                        .header("Authorization", "Bearer " + accessToken))
                .andExpect(status().isNoContent());

        assertThat(eventRepository.findById(saved.getId())).isEmpty();
    }

    private String extractToken(String response, String tokenName) {
        String pattern = "\"" + tokenName + "\":\"";
        int start = response.indexOf(pattern);
        if (start == -1) return null;
        start += pattern.length();
        int end = response.indexOf("\"", start);
        return response.substring(start, end);
    }

}