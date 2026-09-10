package com.mamikos.backend.service;

import com.mamikos.backend.dto.AuthResponse;
import com.mamikos.backend.dto.LoginRequest;
import com.mamikos.backend.dto.RegisterRequest;

public interface AuthService {
    AuthResponse register(RegisterRequest request);
    AuthResponse login(LoginRequest request);
}
