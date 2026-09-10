package com.mamikos.backend.service;

import com.mamikos.backend.model.Role;
import com.mamikos.backend.model.User;
import com.mamikos.backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@EnableScheduling
@RequiredArgsConstructor
public class CreditScheduleService {

    private final UserRepository userRepository;

    // Runs at 00:00:00 on the 1st day of every month
    @Scheduled(cron = "0 0 0 1 * ?")
    @Transactional
    public void rechargeUserCredits() {
        log.info("Starting monthly credit recharge for users...");
        List<User> users = userRepository.findAll();

        for (User user : users) {
            if (user.getRole() == Role.REGULAR_USER) {
                user.setCredits(20);
            } else if (user.getRole() == Role.PREMIUM_USER) {
                user.setCredits(40);
            } else if (user.getRole() == Role.OWNER) {
                user.setCredits(0);
            }
        }

        userRepository.saveAll(users);
        log.info("Monthly credit recharge completed successfully for {} users.", users.size());
    }
}
