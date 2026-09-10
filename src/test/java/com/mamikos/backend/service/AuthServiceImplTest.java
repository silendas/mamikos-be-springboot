package com.mamikos.backend.service;

import com.mamikos.backend.dto.UserProfileResponse;
import com.mamikos.backend.dto.UpdateProfileRequest;
import com.mamikos.backend.dto.ChangePasswordRequest;

import com.mamikos.backend.common.ResponseMessage;
import com.mamikos.backend.dto.AuthResponse;
import com.mamikos.backend.dto.LoginRequest;
import com.mamikos.backend.dto.RegisterRequest;
import com.mamikos.backend.exception.BadRequestException;
import com.mamikos.backend.model.Role;
import com.mamikos.backend.model.User;
import com.mamikos.backend.repository.UserRepository;
import com.mamikos.backend.security.JwtTokenProvider;
import com.mamikos.backend.service.impl.AuthServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private JwtTokenProvider tokenProvider;

    @Mock
    private Authentication authentication;

    @InjectMocks
    private AuthServiceImpl authService;

    private RegisterRequest registerRequest;
    private LoginRequest loginRequest;
    private User user;

    @BeforeEach
    void setUp() {
        registerRequest = new RegisterRequest();
        registerRequest.setUsername("testuser");
        registerRequest.setEmail("test@mamikos.com");
        registerRequest.setPassword("password123");
        registerRequest.setRole(Role.REGULAR_USER);

        loginRequest = new LoginRequest();
        loginRequest.setUsernameOrEmail("testuser");
        loginRequest.setPassword("password123");

        user = User.builder()
                .id(1L)
                .username("testuser")
                .email("test@mamikos.com")
                .password("encodedPassword")
                .role(Role.REGULAR_USER)
                .credits(20)
                .build();
    }

    @Test
    void register_Success() {
        when(userRepository.existsByUsername(registerRequest.getUsername())).thenReturn(false);
        when(userRepository.existsByEmail(registerRequest.getEmail())).thenReturn(false);
        when(passwordEncoder.encode(registerRequest.getPassword())).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(user);
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenReturn(authentication);
        when(tokenProvider.generateToken(authentication)).thenReturn("mockedJwtToken");

        AuthResponse response = authService.register(registerRequest);

        assertThat(response).isNotNull();
        assertThat(response.getAccessToken()).isEqualTo("mockedJwtToken");
        assertThat(response.getUsername()).isEqualTo("testuser");
        assertThat(response.getCredits()).isEqualTo(20);
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void register_UsernameAlreadyExists_ThrowsException() {
        when(userRepository.existsByUsername(registerRequest.getUsername())).thenReturn(true);

        assertThatThrownBy(() -> authService.register(registerRequest))
                .isInstanceOf(BadRequestException.class)
                .hasMessage(ResponseMessage.USERNAME_ALREADY_EXISTS);

        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void register_EmailAlreadyExists_ThrowsException() {
        when(userRepository.existsByUsername(registerRequest.getUsername())).thenReturn(false);
        when(userRepository.existsByEmail(registerRequest.getEmail())).thenReturn(true);

        assertThatThrownBy(() -> authService.register(registerRequest))
                .isInstanceOf(BadRequestException.class)
                .hasMessage(ResponseMessage.EMAIL_ALREADY_EXISTS);

        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void login_Success() {
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenReturn(authentication);
        when(tokenProvider.generateToken(authentication)).thenReturn("mockedJwtToken");
        when(userRepository.findByUsername(loginRequest.getUsernameOrEmail())).thenReturn(Optional.of(user));

        AuthResponse response = authService.login(loginRequest);

        assertThat(response).isNotNull();
        assertThat(response.getAccessToken()).isEqualTo("mockedJwtToken");
        assertThat(response.getUsername()).isEqualTo("testuser");
    }
    @Test
    void getMyProfile_Success() {
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(user));

        UserProfileResponse response = authService.getMyProfile("testuser");

        assertThat(response).isNotNull();
        assertThat(response.getUsername()).isEqualTo("testuser");
        assertThat(response.getEmail()).isEqualTo("test@mamikos.com");
    }

    @Test
    void getMyProfile_UserNotFound_ThrowsException() {
        when(userRepository.findByUsername("unknown")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> authService.getMyProfile("unknown"))
                .isInstanceOf(BadRequestException.class)
                .hasMessage(ResponseMessage.USER_NOT_FOUND);
    }

    @Test
    void updateProfile_Success() {
        UpdateProfileRequest updateRequest = new UpdateProfileRequest("newusername", "new@mamikos.com");
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(user));
        when(userRepository.existsByUsername("newusername")).thenReturn(false);
        when(userRepository.existsByEmail("new@mamikos.com")).thenReturn(false);
        when(userRepository.save(any(User.class))).thenReturn(user);

        UserProfileResponse response = authService.updateProfile("testuser", updateRequest);

        assertThat(response).isNotNull();
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void updateProfile_UsernameAlreadyExists_ThrowsException() {
        UpdateProfileRequest updateRequest = new UpdateProfileRequest("existinguser", "test@mamikos.com");
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(user));
        when(userRepository.existsByUsername("existinguser")).thenReturn(true);

        assertThatThrownBy(() -> authService.updateProfile("testuser", updateRequest))
                .isInstanceOf(BadRequestException.class)
                .hasMessage(ResponseMessage.USERNAME_ALREADY_EXISTS);
    }

    @Test
    void changePassword_Success() {
        ChangePasswordRequest changePasswordRequest = new ChangePasswordRequest("password123", "newpassword123");
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("password123", "encodedPassword")).thenReturn(true);
        when(passwordEncoder.encode("newpassword123")).thenReturn("newEncodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(user);

        authService.changePassword("testuser", changePasswordRequest);

        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void changePassword_IncorrectCurrentPassword_ThrowsException() {
        ChangePasswordRequest changePasswordRequest = new ChangePasswordRequest("wrongpassword", "newpassword123");
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("wrongpassword", "encodedPassword")).thenReturn(false);

        assertThatThrownBy(() -> authService.changePassword("testuser", changePasswordRequest))
                .isInstanceOf(BadRequestException.class)
                .hasMessage(ResponseMessage.INCORRECT_CURRENT_PASSWORD);

        verify(userRepository, never()).save(any(User.class));
    }

}
