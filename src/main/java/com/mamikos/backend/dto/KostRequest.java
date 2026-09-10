package com.mamikos.backend.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class KostRequest {

    @NotBlank(message = "Kost name is required")
    private String name;

    @NotBlank(message = "Location is required")
    private String location;

    @NotNull(message = "Price is required")
    @Min(value = 0, message = "Price must be greater than or equal to 0")
    private Double price;

    private String description;

    @NotNull(message = "Room count is required")
    @Min(value = 1, message = "Room count must be at least 1")
    private Integer roomCount;
}
