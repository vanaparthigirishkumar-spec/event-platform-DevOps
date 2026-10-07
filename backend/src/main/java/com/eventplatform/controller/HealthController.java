package com.eventplatform.controller;

import com.mongodb.client.MongoClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bson.Document;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping
@RequiredArgsConstructor
@Slf4j
public class HealthController {

    private final MongoClient mongoClient;

    @GetMapping("/health")
    public ResponseEntity<Map<String, Object>> liveness() {
        // Liveness: simple check that the application process is alive
        return ResponseEntity.ok(Map.of(
                "status", "UP",
                "check", "liveness",
                "timestamp", java.time.Instant.now().toString()
        ));
    }

    @GetMapping("/ready")
    public ResponseEntity<Map<String, Object>> readiness() {
        // Readiness: check if application is ready to serve traffic
        // This includes checking critical dependencies like MongoDB
        try {
            // Ping MongoDB to verify connection
            mongoClient.getDatabase("admin").runCommand(new Document("ping", 1));

            return ResponseEntity.ok(Map.of(
                    "status", "UP",
                    "check", "readiness",
                    "mongo", "UP",
                    "timestamp", java.time.Instant.now().toString()
            ));
        } catch (Exception e) {
            log.error("Readiness check failed: MongoDB not available - {}", e.getMessage());
            return ResponseEntity.status(503).body(Map.of(
                    "status", "DOWN",
                    "check", "readiness",
                    "reason", "MongoDB not connected",
                    "timestamp", java.time.Instant.now().toString()
            ));
        }
    }

}