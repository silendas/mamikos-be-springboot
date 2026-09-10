package com.mamikos.backend.service;

import com.mamikos.backend.dto.InquiryRequest;
import com.mamikos.backend.dto.InquiryResponse;

import java.util.List;

public interface InquiryService {
    InquiryResponse askAvailability(String username, InquiryRequest request);
    List<InquiryResponse> getUserInquiries(String username);
}
