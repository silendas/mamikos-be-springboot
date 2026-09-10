package com.mamikos.backend.controller;

import com.mamikos.backend.common.BaseResponse;
import com.mamikos.backend.common.ResponseMessage;
import com.mamikos.backend.dto.InquiryRequest;
import com.mamikos.backend.dto.InquiryResponse;
import com.mamikos.backend.service.InquiryService;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/inquiries")
@RequiredArgsConstructor
@Tag(name = "Inquiry", description = "Room availability inquiry and user credit consumption APIs")
public class InquiryController {

    private final InquiryService inquiryService;

    @PostMapping
    @PreAuthorize("hasAnyRole('REGULAR_USER', 'PREMIUM_USER')")
    public ResponseEntity<BaseResponse<InquiryResponse>> askAvailability(
            @Parameter(hidden = true) Principal principal,
            @Valid @RequestBody InquiryRequest request) {
        InquiryResponse response = inquiryService.askAvailability(principal.getName(), request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(BaseResponse.success(HttpStatus.CREATED, ResponseMessage.INQUIRY_SUCCESS, response));
    }

    @GetMapping("/my-inquiries")
    @PreAuthorize("hasAnyRole('REGULAR_USER', 'PREMIUM_USER')")
    public ResponseEntity<BaseResponse<List<InquiryResponse>>> getUserInquiries(@Parameter(hidden = true) Principal principal) {
        List<InquiryResponse> response = inquiryService.getUserInquiries(principal.getName());
        return ResponseEntity.ok(BaseResponse.success(HttpStatus.OK, ResponseMessage.INQUIRIES_FETCHED, response));
    }
}
