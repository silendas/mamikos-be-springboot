package com.mamikos.backend.service;

import com.mamikos.backend.common.ResponseMessage;
import com.mamikos.backend.dto.KostRequest;
import com.mamikos.backend.dto.KostResponse;
import com.mamikos.backend.exception.UnauthorizedException;
import com.mamikos.backend.mapper.KostMapper;
import com.mamikos.backend.model.Kost;
import com.mamikos.backend.model.Role;
import com.mamikos.backend.model.User;
import com.mamikos.backend.repository.KostRepository;
import com.mamikos.backend.repository.UserRepository;
import com.mamikos.backend.service.impl.KostServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class KostServiceImplTest {

    @Mock
    private KostRepository kostRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private KostMapper kostMapper;
    @InjectMocks
    private KostServiceImpl kostService;

    private User owner;
    private User regularUser;
    private Kost kost;
    private KostRequest kostRequest;
    private KostResponse kostResponse;

    @BeforeEach
    void setUp() {
        owner = User.builder().id(1L).username("owner1").email("owner@mamikos.com").role(Role.OWNER).build();
        regularUser = User.builder().id(2L).username("user1").email("user@mamikos.com").role(Role.REGULAR_USER).build();
        kost = Kost.builder().id(1L).name("Kost Mawar").location("Jakarta").price(1500000.0).description("Nice kost").roomCount(5).owner(owner).build();
        kostRequest = new KostRequest();
        kostRequest.setName("Kost Mawar");
        kostRequest.setLocation("Jakarta");
        kostRequest.setPrice(1500000.0);
        kostRequest.setDescription("Nice kost");
        kostRequest.setRoomCount(5);
        kostResponse = KostResponse.builder().id(1L).name("Kost Mawar").location("Jakarta").price(1500000.0).description("Nice kost").roomCount(5).ownerId(1L).ownerName("owner1").build();
    }

    @Test
    void createKost_Success() {
        when(userRepository.findByUsername("owner1")).thenReturn(Optional.of(owner));
        when(kostMapper.toEntity(kostRequest, owner)).thenReturn(kost);
        when(kostRepository.save(kost)).thenReturn(kost);
        when(kostMapper.toResponse(kost)).thenReturn(kostResponse);

        KostResponse response = kostService.createKost("owner1", kostRequest);
        assertThat(response).isNotNull();
        assertThat(response.getName()).isEqualTo("Kost Mawar");
        verify(kostRepository, times(1)).save(kost);
    }

    @Test
    void createKost_UserNotOwner_ThrowsUnauthorized() {
        when(userRepository.findByUsername("user1")).thenReturn(Optional.of(regularUser));
        assertThatThrownBy(() -> kostService.createKost("user1", kostRequest))
                .isInstanceOf(UnauthorizedException.class)
                .hasMessage(ResponseMessage.ONLY_OWNERS_CAN_ADD_KOSTS);
        verify(kostRepository, never()).save(any());
    }

    @Test
    void updateKost_Success() {
        when(kostRepository.findById(1L)).thenReturn(Optional.of(kost));
        when(kostRepository.save(kost)).thenReturn(kost);
        when(kostMapper.toResponse(kost)).thenReturn(kostResponse);

        KostResponse response = kostService.updateKost("owner1", 1L, kostRequest);
        assertThat(response).isNotNull();
        verify(kostRepository, times(1)).save(kost);
    }

    @Test
    void deleteKost_Success() {
        when(kostRepository.findById(1L)).thenReturn(Optional.of(kost));
        doNothing().when(kostRepository).delete(kost);

        kostService.deleteKost("owner1", 1L);
        verify(kostRepository, times(1)).delete(kost);
    }

    @Test
    void getKostById_Success() {
        when(kostRepository.findById(1L)).thenReturn(Optional.of(kost));
        when(kostMapper.toResponse(kost)).thenReturn(kostResponse);

        KostResponse response = kostService.getKostById(1L);
        assertThat(response).isNotNull();
        assertThat(response.getId()).isEqualTo(1L);
    }

    @Test
    @SuppressWarnings("unchecked")
    void searchKosts_Success() {
        Page<Kost> kostPage = new PageImpl<>(List.of(kost));
        when(kostRepository.findAll(any(Specification.class), any(Pageable.class))).thenReturn(kostPage);
        when(kostMapper.toResponse(kost)).thenReturn(kostResponse);

        var response = kostService.searchKosts("Mawar", "Jakarta", 1000000.0, 2000000.0, "asc", 0, 10);
        assertThat(response).isNotNull();
        assertThat(response.getContent()).hasSize(1);
    }
}

