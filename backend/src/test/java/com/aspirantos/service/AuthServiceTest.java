package com.aspirantos.service;

import com.aspirantos.dto.auth.AuthResponse;
import com.aspirantos.dto.auth.LoginRequest;
import com.aspirantos.dto.auth.RegisterRequest;
import com.aspirantos.dto.auth.UserResponse;
import com.aspirantos.entity.Role;
import com.aspirantos.entity.User;
import com.aspirantos.exception.DuplicateEmailException;
import com.aspirantos.exception.InvalidCredentialsException;
import com.aspirantos.repository.UserRepository;
import com.aspirantos.security.JwtService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private JwtService jwtService;

    private PasswordEncoder passwordEncoder;
    private AuthService authService;

    @BeforeEach
    void setUp() {
        passwordEncoder = new BCryptPasswordEncoder(10);
        authService = new AuthServiceImpl(userRepository, passwordEncoder, authenticationManager, jwtService);
    }

    @Test
    @DisplayName("Should successfully register user and hash password using BCrypt")
    void shouldRegisterUserWithHashedPassword() {
        RegisterRequest request = new RegisterRequest(
                "Aarav", "Sharma", "aarav@example.com", "SecurePassword123"
        );

        when(userRepository.existsByEmailIgnoreCase("aarav@example.com")).thenReturn(false);

        User savedUser = User.builder()
                .id(UUID.randomUUID())
                .firstName("Aarav")
                .lastName("Sharma")
                .email("aarav@example.com")
                .password(passwordEncoder.encode("SecurePassword123"))
                .role(Role.USER)
                .build();

        when(userRepository.save(any(User.class))).thenReturn(savedUser);

        UserResponse response = authService.register(request);

        assertNotNull(response);
        assertEquals("Aarav", response.firstName());
        assertEquals("Sharma", response.lastName());
        assertEquals("aarav@example.com", response.email());
        assertEquals(Role.USER, response.role());

        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(userCaptor.capture());
        User captured = userCaptor.getValue();

        assertNotEquals("SecurePassword123", captured.getPassword());
        assertTrue(passwordEncoder.matches("SecurePassword123", captured.getPassword()));
    }

    @Test
    @DisplayName("Should reject registration with duplicate email")
    void shouldRejectDuplicateEmail() {
        RegisterRequest request = new RegisterRequest(
                "Aarav", "Sharma", "existing@example.com", "SecurePassword123"
        );

        when(userRepository.existsByEmailIgnoreCase("existing@example.com")).thenReturn(true);

        assertThrows(DuplicateEmailException.class, () -> authService.register(request));
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    @DisplayName("Should successfully log in with valid credentials and return JWT")
    void shouldLoginSuccessfully() {
        LoginRequest request = new LoginRequest("aarav@example.com", "SecurePassword123");

        User user = User.builder()
                .id(UUID.randomUUID())
                .firstName("Aarav")
                .lastName("Sharma")
                .email("aarav@example.com")
                .password(passwordEncoder.encode("SecurePassword123"))
                .role(Role.USER)
                .build();

        when(userRepository.findByEmailIgnoreCase("aarav@example.com")).thenReturn(Optional.of(user));
        when(jwtService.generateToken(user)).thenReturn("mock.jwt.token");

        AuthResponse response = authService.login(request);

        assertNotNull(response);
        assertEquals("mock.jwt.token", response.accessToken());
        assertEquals("Bearer", response.tokenType());
        assertEquals("aarav@example.com", response.user().email());

        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
    }

    @Test
    @DisplayName("Should throw InvalidCredentialsException when password is wrong")
    void shouldFailLoginWithBadPassword() {
        LoginRequest request = new LoginRequest("aarav@example.com", "WrongPassword");

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenThrow(new BadCredentialsException("Bad credentials"));

        assertThrows(InvalidCredentialsException.class, () -> authService.login(request));
        verify(jwtService, never()).generateToken(any());
    }
}
