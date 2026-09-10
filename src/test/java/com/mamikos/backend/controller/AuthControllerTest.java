package com.mamikos.backend.controller;

import com.mamikos.backend.dto.UserProfileResponse;
import com.mamikos.backend.dto.UpdateProfileRequest;
import com.mamikos.backend.dto.ChangePasswordRequest;
import static org.mockito.Mockito.doNothing;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mamikos.backend.dto.AuthResponse;
import com.mamikos.backend.dto.LoginRequest;
import com.mamikos.backend.dto.RegisterRequest;
import com.mamikos.backend.model.Role;
import com.mamikos.backend.security.CustomUserDetailsService;
import com.mamikos.backend.security.JwtTokenProvider;
import com.mamikos.backend.service.AuthService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AuthController.class)
@AutoConfigureMockMvc(addFilters = false)
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private AuthService authService;

    @MockBean
    private JwtTokenProvider jwtTokenProvider;

    @MockBean
    private CustomUserDetailsService customUserDetailsService;

    @Test
    void register_Success() throws Exception {
        RegisterRequest request = new RegisterRequest();
        request.setUsername("testuser");
        request.setEmail("test@mamikos.com");
        request.setPassword("password123");
        request.setRole(Role.REGULAR_USER);

        AuthResponse authResponse = AuthResponse.builder()
                .accessToken("token123")
                .tokenType("Bearer")
                .id(1L)
                .username("testuser")
                .email("test@mamikos.com")
                .role(Role.REGULAR_USER)
                .credits(20)
                .build();

        when(authService.register(any(RegisterRequest.class))).thenReturn(authResponse);

        mockMvc.perform(post("/api/auth/register")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.accessToken").value("token123"));
    }

    @Test
    void register_InvalidInput_ReturnsBadRequest() throws Exception {
        RegisterRequest request = new RegisterRequest();
        request.setUsername(""); // Invalid blank username
        request.setEmail("invalid-email"); // Invalid email
        request.setPassword("123"); // Too short

        mockMvc.perform(post("/api/auth/register")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void login_Success() throws Exception {
        LoginRequest request = new LoginRequest();
        request.setUsernameOrEmail("testuser");
        request.setPassword("password123");

        AuthResponse authResponse = AuthResponse.builder()
                .accessToken("token123")
                .tokenType("Bearer")
                .id(1L)
                .username("testuser")
                .email("test@mamikos.com")
                .role(Role.REGULAR_USER)
                .credits(20)
                .build();

        when(authService.login(any(LoginRequest.class))).thenReturn(authResponse);

        mockMvc.perform(post("/api/auth/login")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.accessToken").value("token123"));
    }

    @Test
    void login_BadCredentials_ReturnsUnauthorized() throws Exception {
        LoginRequest request = new LoginRequest();
        request.setUsernameOrEmail("testuser");
        request.setPassword("wrongpassword");

        when(authService.login(any(LoginRequest.class)))
                .thenThrow(new org.springframework.security.authentication.BadCredentialsException("Bad credentials"));

        mockMvc.perform(post("/api/auth/login")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("Invalid username or password"));
    }

    @Test
    void getMyProfile_Success() throws Exception {
        UserProfileResponse profileResponse = UserProfileResponse.builder()
                .id(1L)
                .username("testuser")
                .email("test@mamikos.com")
                .role(Role.REGULAR_USER)
                .credits(20)
                .build();

        when(authService.getMyProfile(any())).thenReturn(profileResponse);

        mockMvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get("/api/auth/me")
                        .principal(() -> "testuser"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.username").value("testuser"));
    }

    @Test
    void updateProfile_Success() throws Exception {
        UpdateProfileRequest updateRequest = new UpdateProfileRequest("newusername", "new@mamikos.com");
        UserProfileResponse profileResponse = UserProfileResponse.builder()
                .id(1L)
                .username("newusername")
                .email("new@mamikos.com")
                .role(Role.REGULAR_USER)
                .credits(20)
                .build();

        when(authService.updateProfile(any(), any(UpdateProfileRequest.class))).thenReturn(profileResponse);

        mockMvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put("/api/auth/me")
                        .principal(() -> "testuser")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.username").value("newusername"));
    }

    @Test
    void changePassword_Success() throws Exception {
        ChangePasswordRequest changePasswordRequest = new ChangePasswordRequest("password123", "newpassword123");

        doNothing().when(authService).changePassword(any(), any(ChangePasswordRequest.class));

        mockMvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put("/api/auth/password")
                        .principal(() -> "testuser")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(changePasswordRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }

}

