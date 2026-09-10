package com.mamikos.backend.controller;

import com.mamikos.backend.dto.*;
import java.security.Principal;

import com.mamikos.backend.common.BaseResponse;
import com.mamikos.backend.common.ResponseMessage;
import com.mamikos.backend.dto.AuthResponse;
import com.mamikos.backend.dto.LoginRequest;
import com.mamikos.backend.dto.RegisterRequest;
import com.mamikos.backend.service.AuthService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication", description = "Authentication management APIs (Register & Login)")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<BaseResponse<AuthResponse>> register(@Valid @RequestBody RegisterRequest request) {
        AuthResponse response = authService.register(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(BaseResponse.success(HttpStatus.CREATED, ResponseMessage.REGISTER_SUCCESS, response));
    }

    @PostMapping("/login")
    public ResponseEntity<BaseResponse<AuthResponse>> login(@Valid @RequestBody LoginRequest request) {
        AuthResponse response = authService.login(request);
        return ResponseEntity.ok(BaseResponse.success(HttpStatus.OK, ResponseMessage.LOGIN_SUCCESS, response));
    }
    @GetMapping("/me")
    public ResponseEntity<BaseResponse<UserProfileResponse>> getMyProfile(Principal principal) {
        UserProfileResponse response = authService.getMyProfile(principal.getName());
        return ResponseEntity.ok(BaseResponse.success(HttpStatus.OK, ResponseMessage.USER_PROFILE_FETCHED, response));
    }

    @PutMapping("/me")
    public ResponseEntity<BaseResponse<UserProfileResponse>> updateProfile(
            Principal principal,
            @Valid @RequestBody UpdateProfileRequest request) {
        UserProfileResponse response = authService.updateProfile(principal.getName(), request);
        return ResponseEntity.ok(BaseResponse.success(HttpStatus.OK, ResponseMessage.USER_PROFILE_UPDATED, response));
    }

    @PutMapping("/password")
    public ResponseEntity<BaseResponse<Void>> changePassword(
            Principal principal,
            @Valid @RequestBody ChangePasswordRequest request) {
        authService.changePassword(principal.getName(), request);
        return ResponseEntity.ok(BaseResponse.success(HttpStatus.OK, ResponseMessage.PASSWORD_CHANGED, null));
    }

}
