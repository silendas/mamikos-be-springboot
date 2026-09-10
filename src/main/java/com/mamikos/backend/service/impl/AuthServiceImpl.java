package com.mamikos.backend.service.impl;

import com.mamikos.backend.common.ResponseMessage;
import com.mamikos.backend.dto.AuthResponse;
import com.mamikos.backend.dto.LoginRequest;
import com.mamikos.backend.dto.RegisterRequest;
import com.mamikos.backend.exception.BadRequestException;
import com.mamikos.backend.model.Role;
import com.mamikos.backend.model.User;
import com.mamikos.backend.repository.UserRepository;
import com.mamikos.backend.security.JwtTokenProvider;
import com.mamikos.backend.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider tokenProvider;

    @Override
    @Transactional
    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new BadRequestException(ResponseMessage.USERNAME_ALREADY_EXISTS);
        }
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new BadRequestException(ResponseMessage.EMAIL_ALREADY_EXISTS);
        }

        int initialCredits = 0;
        if (request.getRole() == Role.REGULAR_USER) {
            initialCredits = 20;
        } else if (request.getRole() == Role.PREMIUM_USER) {
            initialCredits = 40;
        } else if (request.getRole() == Role.OWNER) {
            initialCredits = 0;
        }

        User user = User.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(request.getRole())
                .credits(initialCredits)
                .build();

        User savedUser = userRepository.save(user);

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
        );

        String token = tokenProvider.generateToken(authentication);

        return AuthResponse.builder()
                .accessToken(token)
                .tokenType("Bearer")
                .id(savedUser.getId())
                .username(savedUser.getUsername())
                .email(savedUser.getEmail())
                .role(savedUser.getRole())
                .credits(savedUser.getCredits())
                .build();
    }

    @Override
    public AuthResponse login(LoginRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsernameOrEmail(), request.getPassword())
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String token = tokenProvider.generateToken(authentication);

        User user = userRepository.findByUsername(request.getUsernameOrEmail())
                .or(() -> userRepository.findByEmail(request.getUsernameOrEmail()))
                .orElseThrow(() -> new BadRequestException(ResponseMessage.USER_NOT_FOUND));

        return AuthResponse.builder()
                .accessToken(token)
                .tokenType("Bearer")
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .role(user.getRole())
                .credits(user.getCredits())
                .build();
    }
}