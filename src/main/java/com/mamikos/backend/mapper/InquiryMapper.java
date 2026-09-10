package com.mamikos.backend.mapper;

import com.mamikos.backend.dto.InquiryRequest;
import com.mamikos.backend.dto.InquiryResponse;
import com.mamikos.backend.model.Inquiry;
import com.mamikos.backend.model.Kost;
import com.mamikos.backend.model.User;
import org.springframework.stereotype.Component;

@Component
public class InquiryMapper {

    public Inquiry toEntity(InquiryRequest request, User user, Kost kost) {
        return Inquiry.builder()
                .user(user)
                .kost(kost)
                .message(request.getMessage())
                .build();
    }

    public InquiryResponse toResponse(Inquiry inquiry) {
        return InquiryResponse.builder()
                .id(inquiry.getId())
                .userId(inquiry.getUser().getId())
                .username(inquiry.getUser().getUsername())
                .kostId(inquiry.getKost().getId())
                .kostName(inquiry.getKost().getName())
                .message(inquiry.getMessage())
                .createdAt(inquiry.getCreatedAt())
                .build();
    }
}
