package com.mamikos.backend.service.impl;

import com.mamikos.backend.common.ResponseMessage;
import com.mamikos.backend.dto.InquiryRequest;
import com.mamikos.backend.dto.InquiryResponse;
import com.mamikos.backend.exception.BadRequestException;
import com.mamikos.backend.exception.ResourceNotFoundException;
import com.mamikos.backend.mapper.InquiryMapper;
import com.mamikos.backend.model.Inquiry;
import com.mamikos.backend.model.Kost;
import com.mamikos.backend.model.User;
import com.mamikos.backend.repository.InquiryRepository;
import com.mamikos.backend.repository.KostRepository;
import com.mamikos.backend.repository.UserRepository;
import com.mamikos.backend.service.InquiryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class InquiryServiceImpl implements InquiryService {

    private final InquiryRepository inquiryRepository;
    private final UserRepository userRepository;
    private final KostRepository kostRepository;
    private final InquiryMapper inquiryMapper;

    @Override
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

        user.setCredits(user.getCredits() - creditCost);
        userRepository.save(user);

        Inquiry inquiry = inquiryMapper.toEntity(request, user, kost);
        Inquiry savedInquiry = inquiryRepository.save(inquiry);
        return inquiryMapper.toResponse(savedInquiry);
    }

    @Override
    public List<InquiryResponse> getUserInquiries(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException(ResponseMessage.USER_NOT_FOUND));

        return inquiryRepository.findByUserId(user.getId()).stream()
                .map(inquiryMapper::toResponse)
                .collect(Collectors.toList());
    }
}