package com.example.demo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

public interface AvailableRepository extends JpaRepository<Available, Long> {
    Optional<Available> findByUserId(Long userId);

    @Modifying
    @Transactional
    @Query("DELETE FROM Available")
    void deleteAllAvailable();
}

