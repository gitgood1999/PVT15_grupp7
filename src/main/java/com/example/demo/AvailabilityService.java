package com.example.demo;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;


@Service
public class AvailabilityService {

    private final AvailableRepository availableRepository;
    private final UserRepository userRepository;

    public AvailabilityService(AvailableRepository availableRepository, UserRepository userRepository) {
        this.availableRepository = availableRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public void toggleAvailability(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Available available = availableRepository.findByUserId(userId)
                .orElseGet(() -> new Available(false, null, user)); // create if not exists

        boolean newStatus = !available.isAvailable();
        available.setAvailable(newStatus);

        if (newStatus) {
            available.setAvailableSince(LocalDateTime.now());
        } else {
            available.setAvailableSince(null);
        }

        availableRepository.save(available);
    }
}

