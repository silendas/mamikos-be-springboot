package com.mamikos.backend.dto;

import com.mamikos.backend.model.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserProfileResponse {
    private Long id;
    private String username;
    private String email;
    private Role role;
    private Integer credits;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
