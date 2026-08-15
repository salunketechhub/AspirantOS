package com.aspirantos.security;

import com.aspirantos.config.CorsConfig;
import com.aspirantos.config.SecurityConfig;
import com.aspirantos.controller.ProgressController;
import com.aspirantos.dto.progress.OverallProgressResponse;
import com.aspirantos.entity.Role;
import com.aspirantos.entity.User;
import com.aspirantos.service.ProgressService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ProgressController.class)
@Import({
        SecurityConfig.class,
        CorsConfig.class,
        JwtAuthenticationFilter.class,
        JwtAuthenticationEntryPoint.class
})
class ProgressSecurityTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ProgressService progressService;

    @MockitoBean
    private JwtService jwtService;

    @MockitoBean
    private UserDetailsService userDetailsService;

    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = User.builder()
                .id(UUID.randomUUID())
                .firstName("Aarav")
                .lastName("Sharma")
                .email("aarav.sharma@example.com")
                .password("encodedPassword")
                .role(Role.USER)
                .build();
    }

    @Test
    @DisplayName("GET /api/progress without token should return 401 Unauthorized")
    void getProgressWithoutTokenShouldReturn401() throws Exception {
        mockMvc.perform(get("/api/progress"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.status").value(401))
                .andExpect(jsonPath("$.error").value("Unauthorized"));
    }

    @Test
    @DisplayName("PUT /api/progress/topics/{id} without token should return 401 Unauthorized")
    void putProgressWithoutTokenShouldReturn401() throws Exception {
        mockMvc.perform(put("/api/progress/topics/" + UUID.randomUUID())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"status\": \"COMPLETED\"}"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.status").value(401));
    }

    @Test
    @DisplayName("GET /api/progress with valid Bearer token should return 200 OK")
    void getProgressWithValidTokenShouldReturn200() throws Exception {
        when(jwtService.extractUsername("valid.jwt.token")).thenReturn("aarav.sharma@example.com");
        when(userDetailsService.loadUserByUsername("aarav.sharma@example.com")).thenReturn(testUser);
        when(jwtService.isTokenValid("valid.jwt.token", testUser)).thenReturn(true);

        when(progressService.getOverallProgress()).thenReturn(
                OverallProgressResponse.builder()
                        .totalTopics(35)
                        .completedTopics(10)
                        .inProgressTopics(5)
                        .notStartedTopics(20)
                        .completionPercentage(29)
                        .prelimsPercentage(30)
                        .mainsPercentage(28)
                        .optionalPercentage(0)
                        .build()
        );

        mockMvc.perform(get("/api/progress")
                        .header("Authorization", "Bearer valid.jwt.token"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalTopics").value(35))
                .andExpect(jsonPath("$.completedTopics").value(10));
    }
}
