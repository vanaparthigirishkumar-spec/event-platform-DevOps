package com.eventplatform.security;

import com.eventplatform.model.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@Slf4j
public class JwtTokenProvider {

    @Value("${spring.security.jwt.access-secret}")
    private String accessSecret;

    @Value("${spring.security.jwt.refresh-secret}")
    private String refreshSecret;

    @Value("${spring.security.jwt.access-expiry:15m}")
    private String accessExpiry;

    @Value("${spring.security.jwt.refresh-expiry:7d}")
    private String refreshExpiry;

    private SecretKey accessKey;
    private SecretKey refreshKey;
    private long accessExpiryMs;
    private long refreshExpiryMs;

    @PostConstruct
    public void init() {
        this.accessKey = Keys.hmacShaKeyFor(accessSecret.getBytes(StandardCharsets.UTF_8));
        this.refreshKey = Keys.hmacShaKeyFor(refreshSecret.getBytes(StandardCharsets.UTF_8));
        this.accessExpiryMs = parseExpiry(accessExpiry);
        this.refreshExpiryMs = parseExpiry(refreshExpiry);
        log.debug("JWT keys initialized");
    }

    private long parseExpiry(String expiry) {
        if (expiry.endsWith("m")) {
            return Long.parseLong(expiry.replace("m", "")) * 60 * 1000;
        } else if (expiry.endsWith("h")) {
            return Long.parseLong(expiry.replace("h", "")) * 60 * 60 * 1000;
        } else if (expiry.endsWith("d")) {
            return Long.parseLong(expiry.replace("d", "")) * 24 * 60 * 60 * 1000;
        }
        return 15 * 60 * 1000; // default 15m
    }

    public String generateAccessToken(Authentication authentication) {
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        User user = userDetails.getUser();

        Instant now = Instant.now();
        Instant expiry = now.plusMillis(accessExpiryMs);

        List<String> roles = user.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());

        return Jwts.builder()
                .subject(user.getEmail())
                .claim("userId", user.getId())
                .claim("name", user.getName())
                .claim("roles", roles)
                .claim("type", "access")
                .issuedAt(Date.from(now))
                .expiration(Date.from(expiry))
                .signWith(accessKey)
                .compact();
    }

    public String generateRefreshToken(Authentication authentication) {
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        User user = userDetails.getUser();

        Instant now = Instant.now();
        Instant expiry = now.plusMillis(refreshExpiryMs);

        return Jwts.builder()
                .subject(user.getEmail())
                .claim("userId", user.getId())
                .claim("type", "refresh")
                .issuedAt(Date.from(now))
                .expiration(Date.from(expiry))
                .signWith(refreshKey)
                .compact();
    }

    public boolean validateAccessToken(String token) {
        return validateToken(token, accessKey, "access");
    }

    public boolean validateRefreshToken(String token) {
        return validateToken(token, refreshKey, "refresh");
    }

    private boolean validateToken(String token, SecretKey key, String expectedType) {
        try {
            Claims claims = Jwts.parser()
                    .verifyWith(key)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();

            String type = claims.get("type", String.class);
            return expectedType.equals(type) && !claims.getExpiration().before(new Date());
        } catch (Exception e) {
            log.debug("Token validation failed: {}", e.getMessage());
            return false;
        }
    }

    public Claims parseAccessToken(String token) {
        return Jwts.parser()
                .verifyWith(accessKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public Claims parseRefreshToken(String token) {
        return Jwts.parser()
                .verifyWith(refreshKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public String getUserIdFromAccessToken(String token) {
        return parseAccessToken(token).get("userId", String.class);
    }

    public String getEmailFromAccessToken(String token) {
        return parseAccessToken(token).getSubject();
    }

    public long getAccessExpiryMs() {
        return accessExpiryMs;
    }

    public long getRefreshExpiryMs() {
        return refreshExpiryMs;
    }

}