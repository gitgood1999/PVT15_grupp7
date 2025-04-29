package com.example.demo;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AvailableRepository extends JpaRepository<Available, Long> {
    Optional<Available> findByUserId(Long userId);
}

