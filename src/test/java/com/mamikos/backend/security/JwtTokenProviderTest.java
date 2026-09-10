package com.mamikos.backend.security;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.ArrayList;

import static org.assertj.core.api.Assertions.assertThat;

class JwtTokenProviderTest {

    private JwtTokenProvider tokenProvider;

    @BeforeEach
    void setUp() {
        tokenProvider = new JwtTokenProvider();
        // 512-bit secret for HS512 / HS256 base64 encoded
        String base64Secret = "dGhpcy1pcy1hLXZlcnktc2VjdXJlLWFuZC1sb25nLXNlY3JldC1rZXktZm9yLWp3dC1hdXRoZW50aWNhdGlvbi1wdXJwb3Nlcy1hdC1sZWFzdC1zaXh0eS1mb3VyLWJ5dGVz";
        ReflectionTestUtils.setField(tokenProvider, "jwtSecret", base64Secret);
        ReflectionTestUtils.setField(tokenProvider, "jwtExpirationInMs", 86400000L);
    }

    @Test
    void generateAndValidateToken_Success() {
        org.springframework.security.core.userdetails.User principal = 
            new org.springframework.security.core.userdetails.User("testuser", "password", new ArrayList<>());
        Authentication auth = new UsernamePasswordAuthenticationToken(principal, "", principal.getAuthorities());

        String token = tokenProvider.generateToken(auth);
        assertThat(token).isNotNull();

        boolean isValid = tokenProvider.validateToken(token);
        assertThat(isValid).isTrue();

        String username = tokenProvider.getUsernameFromJWT(token);
        assertThat(username).isEqualTo("testuser");
    }

    @Test
    void validateToken_InvalidToken_ReturnsFalse() {
        boolean isValid = tokenProvider.validateToken("invalid.token.string");
        assertThat(isValid).isFalse();
    }
}
