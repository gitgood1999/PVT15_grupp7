package com.example.demo;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.Optional;


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

        Optional<Available> optionalAvailable = availableRepository.findByUserId(userId);
        Available available = optionalAvailable.orElseGet(() -> {
            Available newAvailable = new Available();
            newAvailable.setUser(user);
            newAvailable.setAvailable(false);
            return newAvailable;
        });

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

