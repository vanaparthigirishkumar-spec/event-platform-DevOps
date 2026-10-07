package com.eventplatform;

import com.eventplatform.dto.auth.AuthResponse;
import com.eventplatform.dto.auth.LoginRequest;
import com.eventplatform.dto.auth.RegisterRequest;
import com.eventplatform.dto.event.EventCreateRequest;
import com.eventplatform.dto.event.EventResponse;
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
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
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
class AuthControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EventRepository eventRepository;

    private String accessToken;
    private String refreshToken;

    @BeforeEach
    void setUp() throws Exception {
        userRepository.deleteAll();
        eventRepository.deleteAll();

        // Register a test user
        RegisterRequest registerRequest = RegisterRequest.builder()
                .email("test@example.com")
                .password("password123")
                .name("Test User")
                .build();

        MvcResult result = mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "email": "test@example.com",
                                    "password": "password123",
                                    "name": "Test User"
                                }
                                """))
                .andExpect(status().isCreated())
                .andReturn();

        String response = result.getResponse().getContentAsString();
        accessToken = extractToken(response, "accessToken");
        refreshToken = extractToken(response, "refreshToken");
    }

    @Test
    void register_ShouldCreateUserAndReturnTokens() throws Exception {
        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "email": "newuser@example.com",
                                    "password": "password123",
                                    "name": "New User"
                                }
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.user.email").value("newuser@example.com"));
    }

    @Test
    void register_ShouldFail_WhenEmailExists() throws Exception {
        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "email": "test@example.com",
                                    "password": "password123",
                                    "name": "Test User"
                                }
                                """))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.success").value(false));
    }

    @Test
    void login_ShouldReturnTokens() throws Exception {
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "email": "test@example.com",
                                    "password": "password123"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.accessToken").exists())
                .andExpect(jsonPath("$.data.refreshToken").exists());
    }

    @Test
    void login_ShouldFail_WithWrongPassword() throws Exception {
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "email": "test@example.com",
                                    "password": "wrongpassword"
                                }
                                """))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void refresh_ShouldReturnNewTokens() throws Exception {
        mockMvc.perform(post("/api/auth/refresh")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"refreshToken\": \"" + refreshToken + "\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.accessToken").exists());
    }

    @Test
    void me_ShouldReturnUserInfo() throws Exception {
        mockMvc.perform(get("/api/auth/me")
                        .header("Authorization", "Bearer " + accessToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.email").value("test@example.com"));
    }

    private String extractToken(String response, String tokenName) {
        // Simple extraction - in real tests use ObjectMapper
        String pattern = "\"" + tokenName + "\":\"";
        int start = response.indexOf(pattern);
        if (start == -1) return null;
        start += pattern.length();
        int end = response.indexOf("\"", start);
        return response.substring(start, end);
    }

}