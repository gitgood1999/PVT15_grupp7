package com.example.demo;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface MessageRepository extends JpaRepository<Message, Long> {
    // Leta på chat.id och sortera på timestamp
    List<Message> findByChat_IdOrderByTimestampAsc(Long chatId);
}
