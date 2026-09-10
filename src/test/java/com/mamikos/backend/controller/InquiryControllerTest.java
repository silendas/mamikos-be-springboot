package com.mamikos.backend.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mamikos.backend.dto.InquiryRequest;
import com.mamikos.backend.dto.InquiryResponse;
import com.mamikos.backend.security.CustomUserDetailsService;
import com.mamikos.backend.security.JwtTokenProvider;
import com.mamikos.backend.service.InquiryService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(InquiryController.class)
@AutoConfigureMockMvc(addFilters = false)
class InquiryControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private InquiryService inquiryService;

    @MockBean
    private JwtTokenProvider jwtTokenProvider;

    @MockBean
    private CustomUserDetailsService customUserDetailsService;

    @Test
    void askAvailability_Success() throws Exception {
        InquiryRequest request = new InquiryRequest();
        request.setKostId(1L);
        request.setMessage("Is this available?");

        InquiryResponse response = InquiryResponse.builder()
                .id(1L)
                .userId(1L)
                .username("user1")
                .kostId(1L)
                .kostName("Kost Mawar")
                .message("Is this available?")
                .build();

        when(inquiryService.askAvailability(anyString(), any(InquiryRequest.class))).thenReturn(response);

        mockMvc.perform(post("/api/inquiries")
                        .principal(() -> "user1")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.message").value("Is this available?"));
    }

    @Test
    void getUserInquiries_Success() throws Exception {
        InquiryResponse response = InquiryResponse.builder()
                .id(1L)
                .userId(1L)
                .username("user1")
                .kostId(1L)
                .kostName("Kost Mawar")
                .message("Is this available?")
                .build();

        when(inquiryService.getUserInquiries("user1")).thenReturn(List.of(response));

        mockMvc.perform(get("/api/inquiries/my-inquiries")
                        .principal(() -> "user1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data[0].id").value(1L));
    }
}

