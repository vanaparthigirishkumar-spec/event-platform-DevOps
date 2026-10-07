package com.eventplatform.service;

import com.eventplatform.dto.camp.CampCreateRequest;
import com.eventplatform.dto.camp.CampResponse;
import com.eventplatform.dto.camp.CampUpdateRequest;
import com.eventplatform.dto.common.PageResponse;
import com.eventplatform.exception.ApiException;
import com.eventplatform.model.Camp;
import com.eventplatform.model.User;
import com.eventplatform.model.enums.CampType;
import com.eventplatform.model.enums.UserRole;
import com.eventplatform.repository.CampRepository;
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
class CampServiceTest {

    @Mock
    private CampRepository campRepository;

    @Mock
    private SecurityService securityService;

    @InjectMocks
    private CampService campService;

    private User testUser;
    private Camp testCamp;
    private CampCreateRequest createRequest;
    private CampUpdateRequest updateRequest;
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

        testCamp = Camp.builder()
                .id("camp-id-123")
                .title("Test Camp")
                .description("Test Description")
                .type(CampType.DAY)
                .startDate(Instant.now().plusSeconds(3600))
                .endDate(Instant.now().plusSeconds(7200))
                .location("Test Location")
                .ageGroup("10-15")
                .capacity(50)
                .price(100.0)
                .organizer(testUser)
                .published(true)
                .build();

        createRequest = CampCreateRequest.builder()
                .title("New Camp")
                .description("New Description")
                .type(CampType.OVERNIGHT)
                .startDate(Instant.now().plusSeconds(7200))
                .endDate(Instant.now().plusSeconds(10800))
                .location("New Location")
                .ageGroup("8-12")
                .capacity(30)
                .price(150.0)
                .published(true)
                .build();

        updateRequest = CampUpdateRequest.builder()
                .title("Updated Title")
                .build();

        authentication = mock(Authentication.class);
        when(authentication.getPrincipal()).thenReturn(testUserDetails);
    }

    @Test
    void create_ShouldCreateCampAndReturnResponse() {
        when(campRepository.save(any(Camp.class))).thenAnswer(invocation -> {
            Camp saved = invocation.getArgument(0);
            saved.setId("new-camp-id");
            return saved;
        });

        var response = campService.create(createRequest, authentication);

        assertThat(response).isNotNull();
        assertThat(response.getTitle()).isEqualTo("New Camp");
        assertThat(response.getType()).isEqualTo(CampType.OVERNIGHT);
        verify(campRepository).save(any(Camp.class));
    }

    @Test
    void list_ShouldReturnPagedCamps() {
        PageRequest pageable = PageRequest.of(0, 10, Sort.by(Sort.Direction.ASC, "startDate"));
        Page<Camp> campPage = new PageImpl<>(List.of(testCamp), pageable, 1);
        when(campRepository.findByPublishedTrue(pageable)).thenReturn(campPage);

        var response = campService.list(0, 10, "startDate", "asc", null, null, true);

        assertThat(response).isNotNull();
        assertThat(response.getContent()).hasSize(1);
        assertThat(response.getTotalElements()).isEqualTo(1);
    }

    @Test
    void getById_ShouldReturnCamp_WhenExists() {
        when(campRepository.findById("camp-id-123")).thenReturn(Optional.of(testCamp));

        var response = campService.getById("camp-id-123");

        assertThat(response).isNotNull();
        assertThat(response.getId()).isEqualTo("camp-id-123");
    }

    @Test
    void getById_ShouldThrowException_WhenNotExists() {
        when(campRepository.findById("non-existent")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> campService.getById("non-existent"))
                .isInstanceOf(ApiException.class)
                .hasMessage("Camp not found");
    }

    @Test
    void update_ShouldUpdateCamp_WhenAuthorized() {
        when(securityService.canModifyCamp("camp-id-123", authentication)).thenReturn(true);
        when(campRepository.findById("camp-id-123")).thenReturn(Optional.of(testCamp));
        when(campRepository.save(any(Camp.class))).thenReturn(testCamp);

        var response = campService.update("camp-id-123", updateRequest, authentication);

        assertThat(response).isNotNull();
        assertThat(response.getTitle()).isEqualTo("Updated Title");
    }

    @Test
    void update_ShouldThrowException_WhenNotAuthorized() {
        when(securityService.canModifyCamp("camp-id-123", authentication)).thenReturn(false);

        assertThatThrownBy(() -> campService.update("camp-id-123", updateRequest, authentication))
                .isInstanceOf(ApiException.class)
                .hasMessage("You do not have permission to update this camp");
    }

    @Test
    void delete_ShouldDeleteCamp_WhenAuthorized() {
        when(securityService.canModifyCamp("camp-id-123", authentication)).thenReturn(true);
        when(campRepository.existsById("camp-id-123")).thenReturn(true);

        campService.delete("camp-id-123", authentication);

        verify(campRepository).deleteById("camp-id-123");
    }

    @Test
    void delete_ShouldThrowException_WhenNotAuthorized() {
        when(securityService.canModifyCamp("camp-id-123", authentication)).thenReturn(false);

        assertThatThrownBy(() -> campService.delete("camp-id-123", authentication))
                .isInstanceOf(ApiException.class)
                .hasMessage("You do not have permission to delete this camp");
    }

    @Test
    void getMyCamps_ShouldReturnPagedCamps() {
        PageRequest pageable = PageRequest.of(0, 10, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<Camp> campsPage = new PageImpl<>(List.of(testCamp), pageable, 1);
        when(campRepository.findByOrganizer(testUser, pageable)).thenReturn(campsPage);

        var response = campService.getMyCamps(0, 10, authentication);

        assertThat(response).isNotNull();
        assertThat(response.getContent()).hasSize(1);
        assertThat(response.getTotalElements()).isEqualTo(1);
    }

}