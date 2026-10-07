package com.eventplatform;

import com.eventplatform.model.Camp;
import com.eventplatform.model.User;
import com.eventplatform.model.enums.CampType;
import com.eventplatform.model.enums.UserRole;
import com.eventplatform.repository.CampRepository;
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
class CampControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CampRepository campRepository;

    private String accessToken;
    private User testUser;

    @BeforeEach
    void setUp() throws Exception {
        userRepository.deleteAll();
        campRepository.deleteAll();

        testUser = User.builder()
                .email("camporganizer@example.com")
                .passwordHash("hashed")
                .name("Camp Organizer")
                .role(UserRole.USER)
                .build();
        testUser = userRepository.save(testUser);

        String loginResponse = mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "email": "camporganizer@example.com",
                                    "password": "password123"
                                }
                                """))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        accessToken = extractToken(loginResponse, "accessToken");
    }

    @Test
    void createCamp_ShouldCreateAndReturnCamp() throws Exception {
        mockMvc.perform(post("/api/camps")
                        .header("Authorization", "Bearer " + accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "title": "Test Camp",
                                    "description": "This is a test camp description",
                                    "type": "DAY",
                                    "startDate": "2099-06-01T09:00:00Z",
                                    "endDate": "2099-06-01T17:00:00Z",
                                    "location": "Camp Location",
                                    "ageGroup": "10-15",
                                    "capacity": 30,
                                    "price": 150.0,
                                    "published": true
                                }
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.title").value("Test Camp"))
                .andExpect(jsonPath("$.data.type").value("DAY"));
    }

    @Test
    void listCamps_ShouldReturnPagedCamps() throws Exception {
        Camp camp = Camp.builder()
                .title("Listed Camp")
                .description("Camp for listing")
                .type(CampType.DAY)
                .startDate(Instant.parse("2099-07-01T09:00:00Z"))
                .endDate(Instant.parse("2099-07-01T17:00:00Z"))
                .location("List Camp Location")
                .ageGroup("8-12")
                .capacity(25)
                .price(100.0)
                .organizer(testUser)
                .published(true)
                .build();
        campRepository.save(camp);

        mockMvc.perform(get("/api/camps")
                        .param("page", "0")
                        .param("size", "10")
                        .param("published", "true"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.content").isArray())
                .andExpect(jsonPath("$.data.totalElements").value(1));
    }

    @Test
    void getCamp_ShouldReturnCamp() throws Exception {
        Camp camp = Camp.builder()
                .title("Get Camp")
                .description("Camp to get")
                .type(CampType.OVERNIGHT)
                .startDate(Instant.parse("2099-08-01T09:00:00Z"))
                .endDate(Instant.parse("2099-08-01T17:00:00Z"))
                .location("Get Camp Location")
                .ageGroup("12-16")
                .capacity(20)
                .price(200.0)
                .organizer(testUser)
                .published(true)
                .build();
        Camp saved = campRepository.save(camp);

        mockMvc.perform(get("/api/camps/" + saved.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.id").value(saved.getId()));
    }

    @Test
    void updateCamp_ShouldUpdateWhenOwner() throws Exception {
        Camp camp = Camp.builder()
                .title("Original Camp")
                .description("Original")
                .type(CampType.DAY)
                .startDate(Instant.parse("2099-09-01T09:00:00Z"))
                .endDate(Instant.parse("2099-09-01T17:00:00Z"))
                .location("Original Camp Location")
                .ageGroup("8-12")
                .capacity(20)
                .price(100.0)
                .organizer(testUser)
                .published(true)
                .build();
        Camp saved = campRepository.save(camp);

        mockMvc.perform(put("/api/camps/" + saved.getId())
                        .header("Authorization", "Bearer " + accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "title": "Updated Camp Title"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.title").value("Updated Camp Title"));
    }

    @Test
    void deleteCamp_ShouldDeleteWhenOwner() throws Exception {
        Camp camp = Camp.builder()
                .title("Camp To Delete")
                .description("Will be deleted")
                .type(CampType.DAY)
                .startDate(Instant.parse("2099-10-01T09:00:00Z"))
                .endDate(Instant.parse("2099-10-01T17:00:00Z"))
                .location("Delete Camp Location")
                .ageGroup("10-15")
                .capacity(15)
                .price(120.0)
                .organizer(testUser)
                .published(true)
                .build();
        Camp saved = campRepository.save(camp);

        mockMvc.perform(delete("/api/camps/" + saved.getId())
                        .header("Authorization", "Bearer " + accessToken))
                .andExpect(status().isNoContent());

        assertThat(campRepository.findById(saved.getId())).isEmpty();
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