package com.mamikos.backend.service;

import com.mamikos.backend.common.ResponseMessage;
import com.mamikos.backend.dto.InquiryRequest;
import com.mamikos.backend.dto.InquiryResponse;
import com.mamikos.backend.exception.BadRequestException;
import com.mamikos.backend.mapper.InquiryMapper;
import com.mamikos.backend.model.Inquiry;
import com.mamikos.backend.model.Kost;
import com.mamikos.backend.model.Role;
import com.mamikos.backend.model.User;
import com.mamikos.backend.repository.InquiryRepository;
import com.mamikos.backend.repository.KostRepository;
import com.mamikos.backend.repository.UserRepository;
import com.mamikos.backend.service.impl.InquiryServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class InquiryServiceImplTest {

    @Mock
    private InquiryRepository inquiryRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private KostRepository kostRepository;
    @Mock
    private InquiryMapper inquiryMapper;
    @InjectMocks
    private InquiryServiceImpl inquiryService;

    private User regularUser;
    private User poorUser;
    private Kost kost;
    private InquiryRequest inquiryRequest;
    private Inquiry inquiry;
    private InquiryResponse inquiryResponse;

    @BeforeEach
    void setUp() {
        regularUser = User.builder().id(1L).username("user1").email("user1@mamikos.com").role(Role.REGULAR_USER).credits(20).build();
        poorUser = User.builder().id(2L).username("pooruser").email("poor@mamikos.com").role(Role.REGULAR_USER).credits(2).build();

        kost = Kost.builder().id(1L).name("Kost Mawar").location("Jakarta").price(1500000.0).build();

        inquiryRequest = new InquiryRequest();
        inquiryRequest.setKostId(1L);
        inquiryRequest.setMessage("Is this available?");

        inquiry = Inquiry.builder().id(1L).user(regularUser).kost(kost).message("Is this available?").build();

        inquiryResponse = InquiryResponse.builder().id(1L).userId(1L).username("user1").kostId(1L).kostName("Kost Mawar").message("Is this available?").build();
    }

    @Test
    void askAvailability_Success() {
        when(userRepository.findByUsername("user1")).thenReturn(Optional.of(regularUser));
        when(kostRepository.findById(1L)).thenReturn(Optional.of(kost));
        when(userRepository.save(regularUser)).thenReturn(regularUser);
        when(inquiryMapper.toEntity(inquiryRequest, regularUser, kost)).thenReturn(inquiry);
        when(inquiryRepository.save(inquiry)).thenReturn(inquiry);
        when(inquiryMapper.toResponse(inquiry)).thenReturn(inquiryResponse);

        InquiryResponse response = inquiryService.askAvailability("user1", inquiryRequest);

        assertThat(response).isNotNull();
        assertThat(response.getMessage()).isEqualTo("Is this available?");
        assertThat(regularUser.getCredits()).isEqualTo(15); // Deducted by 5
        verify(inquiryRepository, times(1)).save(inquiry);
    }

    @Test
    void askAvailability_InsufficientCredits_ThrowsBadRequest() {
        when(userRepository.findByUsername("pooruser")).thenReturn(Optional.of(poorUser));
        when(kostRepository.findById(1L)).thenReturn(Optional.of(kost));

        assertThatThrownBy(() -> inquiryService.askAvailability("pooruser", inquiryRequest))
                .isInstanceOf(BadRequestException.class)
                .hasMessage(ResponseMessage.INSUFFICIENT_CREDITS);

        verify(inquiryRepository, never()).save(any());
    }

    @Test
    void getUserInquiries_Success() {
        when(userRepository.findByUsername("user1")).thenReturn(Optional.of(regularUser));
        when(inquiryRepository.findByUserId(1L)).thenReturn(List.of(inquiry));
        when(inquiryMapper.toResponse(inquiry)).thenReturn(inquiryResponse);

        List<InquiryResponse> responses = inquiryService.getUserInquiries("user1");

        assertThat(responses).isNotNull();
        assertThat(responses).hasSize(1);
    }
}
