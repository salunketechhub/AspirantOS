package com.aspirantos.security;

import com.aspirantos.config.CorsConfig;
import com.aspirantos.config.SecurityConfig;
import com.aspirantos.controller.AuthController;
import com.aspirantos.controller.HealthController;
import com.aspirantos.dto.HealthResponse;
import com.aspirantos.dto.auth.UserResponse;
import com.aspirantos.entity.Role;
import com.aspirantos.entity.User;
import com.aspirantos.service.AuthService;
import com.aspirantos.service.HealthService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;
import java.util.UUID;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = {AuthController.class, HealthController.class})
@Import({
        SecurityConfig.class,
        CorsConfig.class,
        JwtAuthenticationFilter.class,
        JwtAuthenticationEntryPoint.class
})
class SecurityFilterChainTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private JwtService jwtService;

    @MockitoBean
    private AuthService authService;

    @MockitoBean
    private HealthService healthService;

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
    @DisplayName("GET /api/auth/me without token should return 401 Unauthorized")
    void getMeWithoutTokenShouldReturn401() throws Exception {
        mockMvc.perform(get("/api/auth/me"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.status").value(401))
                .andExpect(jsonPath("$.error").value("Unauthorized"));
    }

    @Test
    @DisplayName("GET /api/auth/me with invalid token should return 401 Unauthorized")
    void getMeWithInvalidTokenShouldReturn401() throws Exception {
        when(jwtService.extractUsername("invalid.token.payload")).thenReturn("aarav.sharma@example.com");
        when(userDetailsService.loadUserByUsername("aarav.sharma@example.com")).thenReturn(testUser);
        when(jwtService.isTokenValid("invalid.token.payload", testUser)).thenReturn(false);

        mockMvc.perform(get("/api/auth/me")
                        .header("Authorization", "Bearer invalid.token.payload"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.status").value(401));
    }

    @Test
    @DisplayName("GET /api/auth/me with valid token should return 200 OK")
    void getMeWithValidTokenShouldReturn200() throws Exception {
        when(jwtService.extractUsername("valid.mock.token")).thenReturn("aarav.sharma@example.com");
        when(userDetailsService.loadUserByUsername("aarav.sharma@example.com")).thenReturn(testUser);
        when(jwtService.isTokenValid("valid.mock.token", testUser)).thenReturn(true);

        when(authService.getCurrentUser()).thenReturn(
                new UserResponse(testUser.getId(), testUser.getFirstName(), testUser.getLastName(), testUser.getEmail(), Role.USER, Instant.now())
        );

        mockMvc.perform(get("/api/auth/me")
                        .header("Authorization", "Bearer valid.mock.token"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("aarav.sharma@example.com"))
                .andExpect(jsonPath("$.firstName").value("Aarav"));
    }

    @Test
    @DisplayName("Public health endpoints should be accessible without token")
    void publicHealthEndpointsShouldBeAccessible() throws Exception {
        when(healthService.getApplicationHealth()).thenReturn(HealthResponse.up());

        mockMvc.perform(get("/api/health"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("UP"));
    }
}
