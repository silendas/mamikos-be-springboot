package com.mamikos.backend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class InquiryRequest {

    @NotNull(message = "Kost ID is required")
    private Long kostId;

    @NotBlank(message = "Message is required")
    private String message;
}
