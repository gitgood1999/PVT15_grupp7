package com.example.demo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.Optional;

public interface UserMatchRepository extends JpaRepository<UserMatch, Long> {

    @Query("SELECT m FROM UserMatch m WHERE (m.user1 = :user1 AND m.user2 = :user2) OR (m.user1 = :user2 AND m.user2 = :user1)")
    UserMatch findByUsers(@Param("user1") User user1, @Param("user2") User user2);


}

