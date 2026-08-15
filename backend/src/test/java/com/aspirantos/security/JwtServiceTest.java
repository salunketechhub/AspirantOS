package com.aspirantos.security;

import com.aspirantos.entity.Role;
import com.aspirantos.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class JwtServiceTest {

    private JwtServiceImpl jwtService;
    private static final String TEST_SECRET = "aspirantos-super-secure-jwt-test-secret-key-256-bits-long!";
    private User testUser;

    @BeforeEach
    void setUp() {
        jwtService = new JwtServiceImpl();
        jwtService.setSecretForTesting(TEST_SECRET);
        jwtService.setExpirationMsForTesting(3600000); // 1 hour

        testUser = User.builder()
                .id(UUID.randomUUID())
                .firstName("Aarav")
                .lastName("Sharma")
                .email("aarav.sharma@example.com")
                .password("encoded_password")
                .role(Role.USER)
                .build();
    }

    @Test
    @DisplayName("Should generate valid JWT token containing subject")
    void shouldGenerateValidToken() {
        String token = jwtService.generateToken(testUser);

        assertNotNull(token);
        assertFalse(token.isEmpty());

        String username = jwtService.extractUsername(token);
        assertEquals("aarav.sharma@example.com", username);
    }

    @Test
    @DisplayName("Should validate token against user details")
    void shouldValidateTokenAgainstUser() {
        String token = jwtService.generateToken(testUser);

        assertTrue(jwtService.isTokenValid(token, testUser));
    }

    @Test
    @DisplayName("Should detect expired token")
    void shouldDetectExpiredToken() {
        jwtService.setExpirationMsForTesting(-1000);
        String expiredToken = jwtService.generateToken(testUser);

        assertTrue(jwtService.isTokenExpired(expiredToken));
        assertFalse(jwtService.isTokenValid(expiredToken, testUser));
    }

    @Test
    @DisplayName("Should fail validation on mismatched user")
    void shouldFailValidationOnDifferentUser() {
        String token = jwtService.generateToken(testUser);

        User otherUser = User.builder()
                .id(UUID.randomUUID())
                .firstName("Priya")
                .lastName("Patel")
                .email("priya.patel@example.com")
                .password("encoded_password")
                .role(Role.USER)
                .build();

        assertFalse(jwtService.isTokenValid(token, otherUser));
    }
}
