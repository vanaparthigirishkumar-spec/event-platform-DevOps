package com.eventplatform.service;

import com.eventplatform.dto.common.PageResponse;
import com.eventplatform.dto.event.EventCreateRequest;
import com.eventplatform.dto.event.EventResponse;
import com.eventplatform.dto.event.EventUpdateRequest;
import com.eventplatform.exception.ApiException;
import com.eventplatform.model.Event;
import com.eventplatform.model.User;
import com.eventplatform.model.enums.EventCategory;
import com.eventplatform.model.enums.UserRole;
import com.eventplatform.repository.EventRepository;
import com.eventplatform.security.CustomUserDetails;
import com.eventplatform.security.SecurityService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class EventServiceTest {

    @Mock
    private EventRepository eventRepository;

    @Mock
    private SecurityService securityService;

    @InjectMocks
    private EventService eventService;

    private User testUser;
    private Event testEvent;
    private EventCreateRequest createRequest;
    private EventUpdateRequest updateRequest;
    private CustomUserDetails testUserDetails;
    private Authentication authentication;

    @BeforeEach
    void setUp() {
        testUser = User.builder()
                .id("user-id-123")
                .email("test@example.com")
                .name("Test User")
                .role(UserRole.USER)
                .build();

        testUserDetails = new CustomUserDetails(testUser);

        testEvent = Event.builder()
                .id("event-id-123")
                .title("Test Event")
                .description("Test Description")
                .category(EventCategory.TECHNOLOGY)
                .startDate(Instant.now().plusSeconds(3600))
                .endDate(Instant.now().plusSeconds(7200))
                .location("Test Location")
                .capacity(100)
                .organizer(testUser)
                .published(true)
                .build();

        createRequest = EventCreateRequest.builder()
                .title("New Event")
                .description("New Description")
                .category(EventCategory.BUSINESS)
                .startDate(Instant.now().plusSeconds(7200))
                .endDate(Instant.now().plusSeconds(10800))
                .location("New Location")
                .capacity(50)
                .published(true)
                .build();

        updateRequest = EventUpdateRequest.builder()
                .title("Updated Title")
                .build();

        authentication = mock(Authentication.class);
        when(authentication.getPrincipal()).thenReturn(testUserDetails);
    }

    @Test
    void create_ShouldCreateEventAndReturnResponse() {
        when(eventRepository.save(any(Event.class))).thenAnswer(invocation -> {
            Event saved = invocation.getArgument(0);
            saved.setId("new-event-id");
            return saved;
        });

        var response = eventService.create(createRequest, authentication);

        assertThat(response).isNotNull();
        assertThat(response.getTitle()).isEqualTo("New Event");
        assertThat(response.getCategory()).isEqualTo(EventCategory.BUSINESS);
        verify(eventRepository).save(any(Event.class));
    }

    @Test
    void list_ShouldReturnPagedEvents() {
        PageRequest pageable = PageRequest.of(0, 10, Sort.by(Sort.Direction.ASC, "startDate"));
        Page<Event> eventPage = new PageImpl<>(List.of(testEvent), pageable, 1);
        when(eventRepository.findByPublishedTrue(pageable)).thenReturn(eventPage);

        var response = eventService.list(0, 10, "startDate", "asc", null, null, true);

        assertThat(response).isNotNull();
        assertThat(response.getContent()).hasSize(1);
        assertThat(response.getTotalElements()).isEqualTo(1);
    }

    @Test
    void getById_ShouldReturnEvent_WhenExists() {
        when(eventRepository.findById("event-id-123")).thenReturn(Optional.of(testEvent));

        var response = eventService.getById("event-id-123");

        assertThat(response).isNotNull();
        assertThat(response.getId()).isEqualTo("event-id-123");
    }

    @Test
    void getById_ShouldThrowException_WhenNotExists() {
        when(eventRepository.findById("non-existent")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> eventService.getById("non-existent"))
                .isInstanceOf(ApiException.class)
                .hasMessage("Event not found");
    }

    @Test
    void update_ShouldUpdateEvent_WhenAuthorized() {
        when(securityService.canModifyEvent("event-id-123", authentication)).thenReturn(true);
        when(eventRepository.findById("event-id-123")).thenReturn(Optional.of(testEvent));
        when(eventRepository.save(any(Event.class))).thenReturn(testEvent);

        var response = eventService.update("event-id-123", updateRequest, authentication);

        assertThat(response).isNotNull();
        assertThat(response.getTitle()).isEqualTo("Updated Title");
    }

    @Test
    void update_ShouldThrowException_WhenNotAuthorized() {
        when(securityService.canModifyEvent("event-id-123", authentication)).thenReturn(false);

        assertThatThrownBy(() -> eventService.update("event-id-123", updateRequest, authentication))
                .isInstanceOf(ApiException.class)
                .hasMessage("You do not have permission to update this event");
    }

    @Test
    void delete_ShouldDeleteEvent_WhenAuthorized() {
        when(securityService.canModifyEvent("event-id-123", authentication)).thenReturn(true);
        when(eventRepository.existsById("event-id-123")).thenReturn(true);

        eventService.delete("event-id-123", authentication);

        verify(eventRepository).deleteById("event-id-123");
    }

    @Test
    void delete_ShouldThrowException_WhenNotAuthorized() {
        when(securityService.canModifyEvent("event-id-123", authentication)).thenReturn(false);

        assertThatThrownBy(() -> eventService.delete("event-id-123", authentication))
                .isInstanceOf(ApiException.class)
                .hasMessage("You do not have permission to delete this event");
    }

    @Test
    void getMyEvents_ShouldReturnPagedEvents() {
        PageRequest pageable = PageRequest.of(0, 10, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<Event> eventsPage = new PageImpl<>(List.of(testEvent), pageable, 1);
        when(eventRepository.findByOrganizer(testUser, pageable)).thenReturn(eventsPage);

        var response = eventService.getMyEvents(0, 10, authentication);

        assertThat(response).isNotNull();
        assertThat(response.getContent()).hasSize(1);
        assertThat(response.getTotalElements()).isEqualTo(1);
    }

}