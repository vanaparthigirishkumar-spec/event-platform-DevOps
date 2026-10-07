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
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final AuthenticationManager authenticationManager;

    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw ApiException.conflict("Email already registered");
        }

        User user = User.builder()
                .email(request.getEmail())
                .passwordHash(passwordEncoder.encode(request.getPassword()))
                .name(request.getName())
                .role(UserRole.USER)
                .createdAt(Instant.now())
                .updatedAt(Instant.now())
                .build();

        user = userRepository.save(user);

        // Authenticate the newly registered user
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );

        return buildAuthResponse(authentication, user);
    }

    public AuthResponse login(LoginRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );

        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        return buildAuthResponse(authentication, userDetails.getUser());
    }

    public AuthResponse refresh(String refreshToken) {
        if (!jwtTokenProvider.validateRefreshToken(refreshToken)) {
            throw ApiException.unauthorized("Invalid refresh token");
        }

        String email = jwtTokenProvider.parseRefreshToken(refreshToken).getSubject();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> ApiException.unauthorized("User not found"));

        Authentication authentication = new UsernamePasswordAuthenticationToken(
                user.getEmail(), null, user.getAuthorities()
        );

        return buildAuthResponse(authentication, user);
    }

    private AuthResponse buildAuthResponse(Authentication authentication, User user) {
        String accessToken = jwtTokenProvider.generateAccessToken(authentication);
        String refreshToken = jwtTokenProvider.generateRefreshToken(authentication);

        return AuthResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .user(AuthResponse.UserInfo.from(user))
                .build();
    }

}