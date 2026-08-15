package com.aspirantos.service;

import com.aspirantos.dto.auth.AuthResponse;
import com.aspirantos.dto.auth.LoginRequest;
import com.aspirantos.dto.auth.RegisterRequest;
import com.aspirantos.dto.auth.UserResponse;
import com.aspirantos.entity.Role;
import com.aspirantos.entity.User;
import com.aspirantos.exception.DuplicateEmailException;
import com.aspirantos.exception.InvalidCredentialsException;
import com.aspirantos.exception.ResourceNotFoundException;
import com.aspirantos.repository.UserRepository;
import com.aspirantos.security.JwtService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthServiceImpl implements AuthService {

    private static final Logger log = LoggerFactory.getLogger(AuthServiceImpl.class);

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    public AuthServiceImpl(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            AuthenticationManager authenticationManager,
            JwtService jwtService
    ) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
    }

    @Override
    @Transactional
    public UserResponse register(RegisterRequest request) {
        String normalizedEmail = request.email().trim().toLowerCase();

        if (userRepository.existsByEmailIgnoreCase(normalizedEmail)) {
            log.warn("Registration failed: Email {} is already registered", normalizedEmail);
            throw new DuplicateEmailException("An account with email " + normalizedEmail + " already exists");
        }

        User user = User.builder()
                .firstName(request.firstName().trim())
                .lastName(request.lastName().trim())
                .email(normalizedEmail)
                .password(passwordEncoder.encode(request.password()))
                .role(Role.USER)
                .build();

        User savedUser = userRepository.save(user);
        log.info("User registered successfully with ID: {}", savedUser.getId());

        return UserResponse.fromUser(savedUser);
    }

    @Override
    public AuthResponse login(LoginRequest request) {
        String normalizedEmail = request.email().trim().toLowerCase();

        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(normalizedEmail, request.password())
            );
        } catch (BadCredentialsException e) {
            log.warn("Authentication failed for email: {}", normalizedEmail);
            throw new InvalidCredentialsException("Invalid email or password");
        }

        User user = userRepository.findByEmailIgnoreCase(normalizedEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + normalizedEmail));

        String accessToken = jwtService.generateToken(user);
        log.info("User logged in successfully: {}", user.getEmail());

        return AuthResponse.of(accessToken, UserResponse.fromUser(user));
    }

    @Override
    @Transactional(readOnly = true)
    public UserResponse getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated() || "anonymousUser".equals(authentication.getPrincipal())) {
            throw new InvalidCredentialsException("No authenticated user found in security context");
        }

        String email = authentication.getName();
        User user = userRepository.findByEmailIgnoreCase(email)
                .orElseThrow(() -> new ResourceNotFoundException("Authenticated user not found in database"));

        return UserResponse.fromUser(user);
    }
}
