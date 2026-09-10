package com.mamikos.backend.service;

import com.mamikos.backend.common.ResponseMessage;
import com.mamikos.backend.dto.InquiryRequest;
import com.mamikos.backend.dto.InquiryResponse;
import com.mamikos.backend.exception.BadRequestException;
import com.mamikos.backend.exception.ResourceNotFoundException;
import com.mamikos.backend.model.Inquiry;
import com.mamikos.backend.model.Kost;
import com.mamikos.backend.model.User;
import com.mamikos.backend.repository.InquiryRepository;
import com.mamikos.backend.repository.KostRepository;
import com.mamikos.backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class InquiryService {

    private final InquiryRepository inquiryRepository;
    private final UserRepository userRepository;
    private final KostRepository kostRepository;

    @Transactional
    public InquiryResponse askAvailability(String username, InquiryRequest request) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException(ResponseMessage.USER_NOT_FOUND));

        Kost kost = kostRepository.findById(request.getKostId())
                .orElseThrow(() -> new ResourceNotFoundException(ResponseMessage.KOST_NOT_FOUND));

        int creditCost = 5;
        if (user.getCredits() < creditCost) {
            throw new BadRequestException(ResponseMessage.INSUFFICIENT_CREDITS);
        }

        // Deduct 5 credits
        user.setCredits(user.getCredits() - creditCost);
        userRepository.save(user);

        Inquiry inquiry = Inquiry.builder()
                .user(user)
                .kost(kost)
                .message(request.getMessage())
                .build();

        Inquiry savedInquiry = inquiryRepository.save(inquiry);
        return mapToResponse(savedInquiry);
    }

    public List<InquiryResponse> getUserInquiries(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException(ResponseMessage.USER_NOT_FOUND));

        return inquiryRepository.findByUserId(user.getId()).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    private InquiryResponse mapToResponse(Inquiry inquiry) {
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
