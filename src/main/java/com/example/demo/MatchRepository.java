package com.example.demo;

import org.springframework.data.jpa.repository.JpaRepository;

public interface MatchRepository extends JpaRepository<UserMatch, Long> {
    UserMatch findById(long matchId);
}
