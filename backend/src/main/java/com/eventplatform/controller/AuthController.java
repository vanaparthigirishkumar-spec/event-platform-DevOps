package com.eventplatform.controller;

import com.eventplatform.dto.auth.AuthResponse;
import com.eventplatform.dto.auth.LoginRequest;
import com.eventplatform.dto.auth.RegisterRequest;
import com.eventplatform.dto.auth.TokenRefreshRequest;
import com.eventplatform.security.CustomUserDetails;
import com.eventplatform.service.AuthService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest request) {
        AuthResponse response = authService.register(request);
        return ResponseEntity.status(201).body(response);
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request,
                                               HttpServletResponse response) {
        AuthResponse authResponse = authService.login(request);
        setAuthCookies(response, authResponse);
        return ResponseEntity.ok(authResponse);
    }

    @PostMapping("/refresh")
    public ResponseEntity<AuthResponse> refresh(@Valid @RequestBody TokenRefreshRequest request,
                                                 HttpServletResponse response) {
        AuthResponse authResponse = authService.refresh(request.getRefreshToken());
        setAuthCookies(response, authResponse);
        return ResponseEntity.ok(authResponse);
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(HttpServletResponse response) {
        clearAuthCookies(response);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/me")
    public ResponseEntity<AuthResponse.UserInfo> me(Authentication authentication) {
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        return ResponseEntity.ok(AuthResponse.UserInfo.from(userDetails.getUser()));
    }

    private void setAuthCookies(HttpServletResponse response, AuthResponse authResponse) {
        Cookie accessCookie = new Cookie("accessToken", authResponse.getAccessToken());
        accessCookie.setHttpOnly(true);
        accessCookie.setSecure(false); // Set to true in production with HTTPS
        accessCookie.setPath("/");
        accessCookie.setMaxAge(15 * 60); // 15 minutes
        accessCookie.setAttribute("SameSite", "Lax");
        response.addCookie(accessCookie);

        Cookie refreshCookie = new Cookie("refreshToken", authResponse.getRefreshToken());
        refreshCookie.setHttpOnly(true);
        refreshCookie.setSecure(false); // Set to true in production with HTTPS
        refreshCookie.setPath("/");
        refreshCookie.setMaxAge(7 * 24 * 60 * 60); // 7 days
        refreshCookie.setAttribute("SameSite", "Lax");
        response.addCookie(refreshCookie);
    }

    private void clearAuthCookies(HttpServletResponse response) {
        Cookie accessCookie = new Cookie("accessToken", "");
        accessCookie.setHttpOnly(true);
        accessCookie.setSecure(false);
        accessCookie.setPath("/");
        accessCookie.setMaxAge(0);
        accessCookie.setAttribute("SameSite", "Lax");
        response.addCookie(accessCookie);

        Cookie refreshCookie = new Cookie("refreshToken", "");
        refreshCookie.setHttpOnly(true);
        refreshCookie.setSecure(false);
        refreshCookie.setPath("/");
        refreshCookie.setMaxAge(0);
        refreshCookie.setAttribute("SameSite", "Lax");
        response.addCookie(refreshCookie);
    }

}