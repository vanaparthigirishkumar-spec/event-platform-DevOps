package com.eventplatform.security;

import com.eventplatform.model.Event;
import com.eventplatform.model.Camp;
import com.eventplatform.model.User;
import com.eventplatform.model.enums.UserRole;
import com.eventplatform.repository.CampRepository;
import com.eventplatform.repository.EventRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.security.core.Authentication;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class SecurityServiceTest {

    @Mock
    private EventRepository eventRepository;

    @Mock
    private CampRepository campRepository;

    @Mock
    private Authentication authentication;

    @InjectMocks
    private SecurityService securityService;

    private User regularUser;
    private User adminUser;
    private Event testEvent;
    private Camp testCamp;

    @BeforeEach
    void setUp() {
        regularUser = User.builder()
                .id("user-id-123")
                .email("user@example.com")
                .role(UserRole.USER)
                .build();

        adminUser = User.builder()
                .id("admin-id-456")
                .email("admin@example.com")
                .role(UserRole.ADMIN)
                .build();

        testEvent = Event.builder()
                .id("event-id-123")
                .organizer(regularUser)
                .build();

        testCamp = Camp.builder()
                .id("camp-id-123")
                .organizer(regularUser)
                .build();
    }

    @Test
    void isEventOwner_ShouldReturnTrue_WhenUserOwnsEvent() {
        when(eventRepository.existsByIdAndOrganizer("event-id-123", regularUser)).thenReturn(true);
        CustomUserDetails userDetails = new CustomUserDetails(regularUser);
        when(authentication.getPrincipal()).thenReturn(userDetails);

        boolean result = securityService.isEventOwner("event-id-123", authentication);

        assertThat(result).isTrue();
    }

    @Test
    void isEventOwner_ShouldReturnFalse_WhenUserDoesNotOwnEvent() {
        when(eventRepository.existsByIdAndOrganizer("event-id-123", regularUser)).thenReturn(false);
        CustomUserDetails userDetails = new CustomUserDetails(regularUser);
        when(authentication.getPrincipal()).thenReturn(userDetails);

        boolean result = securityService.isEventOwner("event-id-123", authentication);

        assertThat(result).isFalse();
    }

    @Test
    void isCampOwner_ShouldReturnTrue_WhenUserOwnsCamp() {
        when(campRepository.existsByIdAndOrganizer("camp-id-123", regularUser)).thenReturn(true);
        CustomUserDetails userDetails = new CustomUserDetails(regularUser);
        when(authentication.getPrincipal()).thenReturn(userDetails);

        boolean result = securityService.isCampOwner("camp-id-123", authentication);

        assertThat(result).isTrue();
    }

    @Test
    void isCampOwner_ShouldReturnFalse_WhenUserDoesNotOwnCamp() {
        when(campRepository.existsByIdAndOrganizer("camp-id-123", regularUser)).thenReturn(false);
        CustomUserDetails userDetails = new CustomUserDetails(regularUser);
        when(authentication.getPrincipal()).thenReturn(userDetails);

        boolean result = securityService.isCampOwner("camp-id-123", authentication);

        assertThat(result).isFalse();
    }

    @Test
    void isAdmin_ShouldReturnTrue_ForAdminUser() {
        CustomUserDetails userDetails = new CustomUserDetails(adminUser);
        when(authentication.getPrincipal()).thenReturn(userDetails);

        boolean result = securityService.isAdmin(authentication);

        assertThat(result).isTrue();
    }

    @Test
    void isAdmin_ShouldReturnFalse_ForRegularUser() {
        CustomUserDetails userDetails = new CustomUserDetails(regularUser);
        when(authentication.getPrincipal()).thenReturn(userDetails);

        boolean result = securityService.isAdmin(authentication);

        assertThat(result).isFalse();
    }

    @Test
    void canModifyEvent_ShouldReturnTrue_ForAdmin() {
        CustomUserDetails userDetails = new CustomUserDetails(adminUser);
        when(authentication.getPrincipal()).thenReturn(userDetails);

        boolean result = securityService.canModifyEvent("event-id-123", authentication);

        assertThat(result).isTrue();
    }

    @Test
    void canModifyEvent_ShouldReturnTrue_ForOwner() {
        CustomUserDetails userDetails = new CustomUserDetails(regularUser);
        when(authentication.getPrincipal()).thenReturn(userDetails);
        when(eventRepository.existsByIdAndOrganizer("event-id-123", regularUser)).thenReturn(true);

        boolean result = securityService.canModifyEvent("event-id-123", authentication);

        assertThat(result).isTrue();
    }

    @Test
    void canModifyEvent_ShouldReturnFalse_ForNonOwnerNonAdmin() {
        User otherUser = User.builder().id("other-id").role(UserRole.USER).build();
        CustomUserDetails userDetails = new CustomUserDetails(otherUser);
        when(authentication.getPrincipal()).thenReturn(userDetails);
        when(eventRepository.existsByIdAndOrganizer("event-id-123", otherUser)).thenReturn(false);

        boolean result = securityService.canModifyEvent("event-id-123", authentication);

        assertThat(result).isFalse();
    }

}