package com.aspirantos.service;

import com.aspirantos.dto.auth.AuthResponse;
import com.aspirantos.dto.auth.LoginRequest;
import com.aspirantos.dto.auth.RegisterRequest;
import com.aspirantos.dto.auth.UserResponse;

public interface AuthService {

    UserResponse register(RegisterRequest request);

    AuthResponse login(LoginRequest request);

    UserResponse getCurrentUser();
}
