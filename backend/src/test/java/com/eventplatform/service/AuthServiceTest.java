package com.eventplatform.service;

import com.eventplatform.dto.auth.AuthResponse;
import com.eventplatform.dto.auth.LoginRequest;
import com.eventplatform.dto.auth.RegisterRequest;
import com.eventplatform.exception.ApiException;
import com.eventplatform.model.User;
import com.eventplatform.model.enums.UserRole;
import com.eventplatform.repository.UserRepository;
import com.eventplatform.security.CustomUserDetails;
import com.eventplatform.security.JwtTokenProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtTokenProvider jwtTokenProvider;

    @Mock
    private AuthenticationManager authenticationManager;

    @InjectMocks
    private AuthService authService;

    private RegisterRequest registerRequest;
    private LoginRequest loginRequest;
    private User testUser;
    private CustomUserDetails testUserDetails;
    private Authentication authentication;

    @BeforeEach
    void setUp() {
        registerRequest = RegisterRequest.builder()
                .email("test@example.com")
                .password("password123")
                .name("Test User")
                .build();

        loginRequest = LoginRequest.builder()
                .email("test@example.com")
                .password("password123")
                .build();

        testUser = User.builder()
                .id("user-id-123")
                .email("test@example.com")
                .passwordHash("hashed-password")
                .name("Test User")
                .role(UserRole.USER)
                .createdAt(java.time.Instant.now())
                .updatedAt(java.time.Instant.now())
                .build();

        testUserDetails = new CustomUserDetails(testUser);

        authentication = mock(Authentication.class);
        when(authentication.getPrincipal()).thenReturn(testUserDetails);
    }

    @Test
    void register_ShouldCreateUserAndReturnTokens() {
        when(userRepository.existsByEmail(registerRequest.getEmail())).thenReturn(false);
        when(passwordEncoder.encode(registerRequest.getPassword())).thenReturn("hashed-password");
        when(userRepository.save(any(User.class))).thenReturn(testUser);
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authentication);
        when(jwtTokenProvider.generateAccessToken(authentication)).thenReturn("access-token");
        when(jwtTokenProvider.generateRefreshToken(authentication)).thenReturn("refresh-token");

        AuthResponse response = authService.register(registerRequest);

        assertThat(response).isNotNull();
        assertThat(response.getAccessToken()).isEqualTo("access-token");
        assertThat(response.getRefreshToken()).isEqualTo("refresh-token");
        assertThat(response.getUser().getEmail()).isEqualTo("test@example.com");
        assertThat(response.getUser().getRole()).isEqualTo(UserRole.USER);

        verify(userRepository).save(any(User.class));
    }

    @Test
    void register_ShouldThrowException_WhenEmailExists() {
        when(userRepository.existsByEmail(registerRequest.getEmail())).thenReturn(true);

        assertThatThrownBy(() -> authService.register(registerRequest))
                .isInstanceOf(ApiException.class)
                .hasMessage("Email already registered");

        verify(userRepository, never()).save(any());
    }

    @Test
    void login_ShouldReturnTokens() {
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authentication);
        when(jwtTokenProvider.generateAccessToken(authentication)).thenReturn("access-token");
        when(jwtTokenProvider.generateRefreshToken(authentication)).thenReturn("refresh-token");

        AuthResponse response = authService.login(loginRequest);

        assertThat(response).isNotNull();
        assertThat(response.getAccessToken()).isEqualTo("access-token");
        assertThat(response.getRefreshToken()).isEqualTo("refresh-token");
    }

    @Test
    void refresh_ShouldReturnNewTokens_WhenValidRefreshToken() {
        String refreshToken = "valid-refresh-token";
        when(jwtTokenProvider.validateRefreshToken(refreshToken)).thenReturn(true);
        
        io.jsonwebtoken.Claims claims = mock(io.jsonwebtoken.Claims.class);
        lenient().when(claims.getSubject()).thenReturn("test@example.com");
        when(jwtTokenProvider.parseRefreshToken(refreshToken)).thenReturn(claims);
        
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(testUser));
        when(jwtTokenProvider.generateAccessToken(any(Authentication.class))).thenReturn("new-access-token");
        when(jwtTokenProvider.generateRefreshToken(any(Authentication.class))).thenReturn("new-refresh-token");

        AuthResponse response = authService.refresh(refreshToken);

        assertThat(response).isNotNull();
        assertThat(response.getAccessToken()).isEqualTo("new-access-token");
        assertThat(response.getRefreshToken()).isEqualTo("new-refresh-token");
    }

    @Test
    void refresh_ShouldThrowException_WhenInvalidRefreshToken() {
        when(jwtTokenProvider.validateRefreshToken("invalid-token")).thenReturn(false);

        assertThatThrownBy(() -> authService.refresh("invalid-token"))
                .isInstanceOf(ApiException.class)
                .hasMessage("Invalid refresh token");
    }

    }