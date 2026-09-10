package com.mamikos.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class InquiryResponse {
    private Long id;
    private Long userId;
    private String username;
    private Long kostId;
    private String kostName;
    private String message;
    private LocalDateTime createdAt;
}
