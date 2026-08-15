package com.aspirantos.controller;

import com.aspirantos.dto.auth.AuthResponse;
import com.aspirantos.dto.auth.LoginRequest;
import com.aspirantos.dto.auth.RegisterRequest;
import com.aspirantos.dto.auth.UserResponse;
import com.aspirantos.entity.Role;
import com.aspirantos.exception.DuplicateEmailException;
import com.aspirantos.exception.GlobalExceptionHandler;
import com.aspirantos.exception.InvalidCredentialsException;
import com.aspirantos.service.AuthService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.Instant;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class AuthControllerTest {

    private MockMvc mockMvc;

    @Mock
    private AuthService authService;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        AuthController controller = new AuthController(authService);
        mockMvc = MockMvcBuilders.standaloneSetup(controller)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    @DisplayName("POST /api/auth/register should return 201 Created")
    void registerShouldReturn201() throws Exception {
        RegisterRequest request = new RegisterRequest("Aarav", "Sharma", "aarav@example.com", "Password123");
        UserResponse response = new UserResponse(UUID.randomUUID(), "Aarav", "Sharma", "aarav@example.com", Role.USER, Instant.now());

        when(authService.register(any(RegisterRequest.class))).thenReturn(response);

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.email").value("aarav@example.com"))
                .andExpect(jsonPath("$.firstName").value("Aarav"))
                .andExpect(jsonPath("$.role").value("USER"));
    }

    @Test
    @DisplayName("POST /api/auth/register with duplicate email should return 409 Conflict")
    void registerWithDuplicateEmailShouldReturn409() throws Exception {
        RegisterRequest request = new RegisterRequest("Aarav", "Sharma", "duplicate@example.com", "Password123");

        when(authService.register(any(RegisterRequest.class)))
                .thenThrow(new DuplicateEmailException("An account with email duplicate@example.com already exists"));

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status").value(409))
                .andExpect(jsonPath("$.error").value("Conflict"))
                .andExpect(jsonPath("$.message").value("An account with email duplicate@example.com already exists"));
    }

    @Test
    @DisplayName("POST /api/auth/register with short password should return 400 Bad Request")
    void registerWithShortPasswordShouldReturn400() throws Exception {
        RegisterRequest request = new RegisterRequest("Aarav", "Sharma", "aarav@example.com", "short");

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.fieldErrors.password").exists());
    }

    @Test
    @DisplayName("POST /api/auth/login with valid credentials should return 200 OK and token")
    void loginShouldReturnToken() throws Exception {
        LoginRequest request = new LoginRequest("aarav@example.com", "Password123");
        UserResponse userResponse = new UserResponse(UUID.randomUUID(), "Aarav", "Sharma", "aarav@example.com", Role.USER, Instant.now());
        AuthResponse authResponse = AuthResponse.of("mock.jwt.token", userResponse);

        when(authService.login(any(LoginRequest.class))).thenReturn(authResponse);

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").value("mock.jwt.token"))
                .andExpect(jsonPath("$.tokenType").value("Bearer"))
                .andExpect(jsonPath("$.user.email").value("aarav@example.com"));
    }

    @Test
    @DisplayName("POST /api/auth/login with bad credentials should return 401 Unauthorized")
    void loginWithBadCredentialsShouldReturn401() throws Exception {
        LoginRequest request = new LoginRequest("aarav@example.com", "WrongPassword");

        when(authService.login(any(LoginRequest.class)))
                .thenThrow(new InvalidCredentialsException("Invalid email or password"));

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.status").value(401))
                .andExpect(jsonPath("$.error").value("Unauthorized"))
                .andExpect(jsonPath("$.message").value("Invalid email or password"));
    }

    @Test
    @DisplayName("GET /api/auth/me should return current user profile")
    void getCurrentUserShouldReturnProfile() throws Exception {
        UserResponse userResponse = new UserResponse(UUID.randomUUID(), "Aarav", "Sharma", "aarav@example.com", Role.USER, Instant.now());

        when(authService.getCurrentUser()).thenReturn(userResponse);

        mockMvc.perform(get("/api/auth/me"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("aarav@example.com"))
                .andExpect(jsonPath("$.firstName").value("Aarav"));
    }
}
