package com.mamikos.backend.mapper;

import com.mamikos.backend.dto.KostRequest;
import com.mamikos.backend.dto.KostResponse;
import com.mamikos.backend.model.Kost;
import com.mamikos.backend.model.User;
import org.springframework.stereotype.Component;

@Component
public class KostMapper {

    public Kost toEntity(KostRequest request, User owner) {
        return Kost.builder()
                .owner(owner)
                .name(request.getName())
                .location(request.getLocation())
                .price(request.getPrice())
                .description(request.getDescription())
                .roomCount(request.getRoomCount())
                .build();
    }

    public KostResponse toResponse(Kost kost) {
        return KostResponse.builder()
                .id(kost.getId())
                .name(kost.getName())
                .location(kost.getLocation())
                .price(kost.getPrice())
                .description(kost.getDescription())
                .roomCount(kost.getRoomCount())
                .ownerId(kost.getOwner().getId())
                .ownerName(kost.getOwner().getUsername())
                .createdAt(kost.getCreatedAt())
                .updatedAt(kost.getUpdatedAt())
                .build();
    }
}
