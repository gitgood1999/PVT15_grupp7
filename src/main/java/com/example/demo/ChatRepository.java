package com.example.demo;


import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ChatRepository extends JpaRepository<Chat, Long> {
    Chat findByMatch(UserMatch match);


}
