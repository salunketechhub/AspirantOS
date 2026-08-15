package com.aspirantos.controller;

import com.aspirantos.dto.DatabaseHealthResponse;
import com.aspirantos.dto.HealthResponse;
import com.aspirantos.service.HealthService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/health")
public class HealthController {

    private final HealthService healthService;

    public HealthController(HealthService healthService) {
        this.healthService = healthService;
    }

    @GetMapping
    public ResponseEntity<HealthResponse> getHealth() {
        return ResponseEntity.ok(healthService.getApplicationHealth());
    }

    @GetMapping("/db")
    public ResponseEntity<DatabaseHealthResponse> getDatabaseHealth() {
        DatabaseHealthResponse response = healthService.getDatabaseHealth();
        if ("UP".equalsIgnoreCase(response.status())) {
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(response);
        }
    }
}
