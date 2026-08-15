package com.aspirantos.controller;

import com.aspirantos.dto.DatabaseHealthResponse;
import com.aspirantos.dto.HealthResponse;
import com.aspirantos.service.HealthService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class HealthControllerTest {

    private MockMvc mockMvc;

    @Mock
    private HealthService healthService;

    @BeforeEach
    void setUp() {
        HealthController controller = new HealthController(healthService);
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
    }

    @Test
    @DisplayName("GET /api/health should return UP status with application name")
    void getHealthShouldReturnUp() throws Exception {
        when(healthService.getApplicationHealth()).thenReturn(HealthResponse.up());

        mockMvc.perform(get("/api/health"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("UP"))
                .andExpect(jsonPath("$.application").value("AspirantOS"))
                .andExpect(jsonPath("$.message").value("Backend is running successfully"));
    }

    @Test
    @DisplayName("GET /api/health/db should return UP status when database is connected")
    void getDatabaseHealthShouldReturnUpWhenConnected() throws Exception {
        when(healthService.getDatabaseHealth()).thenReturn(DatabaseHealthResponse.up());

        mockMvc.perform(get("/api/health/db"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("UP"))
                .andExpect(jsonPath("$.database").value("PostgreSQL"))
                .andExpect(jsonPath("$.message").value("Database connection successful"));
    }

    @Test
    @DisplayName("GET /api/health/db should return 503 SERVICE_UNAVAILABLE when database is down")
    void getDatabaseHealthShouldReturn503WhenDown() throws Exception {
        when(healthService.getDatabaseHealth())
                .thenReturn(DatabaseHealthResponse.down("Connection refused"));

        mockMvc.perform(get("/api/health/db"))
                .andExpect(status().isServiceUnavailable())
                .andExpect(jsonPath("$.status").value("DOWN"))
                .andExpect(jsonPath("$.database").value("PostgreSQL"))
                .andExpect(jsonPath("$.message").value("Database connection failed: Connection refused"));
    }
}
