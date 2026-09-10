package com.mamikos.backend.service;

import com.mamikos.backend.model.Role;
import com.mamikos.backend.model.User;
import com.mamikos.backend.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CreditScheduleServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private CreditScheduleService creditScheduleService;

    @Test
    void rechargeUserCredits_Success() {
        User regular = User.builder().id(1L).role(Role.REGULAR_USER).credits(5).build();
        User premium = User.builder().id(2L).role(Role.PREMIUM_USER).credits(10).build();
        User owner = User.builder().id(3L).role(Role.OWNER).credits(50).build();

        List<User> users = List.of(regular, premium, owner);

        when(userRepository.findAll()).thenReturn(users);
        when(userRepository.saveAll(users)).thenReturn(users);

        creditScheduleService.rechargeUserCredits();

        assertThat(regular.getCredits()).isEqualTo(20);
        assertThat(premium.getCredits()).isEqualTo(40);
        assertThat(owner.getCredits()).isEqualTo(0);

        verify(userRepository, times(1)).findAll();
        verify(userRepository, times(1)).saveAll(users);
    }
}
