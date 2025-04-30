package com.example.demo;


import org.springframework.data.annotation.Id;
import java.time.LocalDateTime;
import jakarta.persistence.*;


@Entity
public class Message {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // ✅ This is your primary key

    private String content;
    private LocalDateTime timestamp;

    @ManyToOne
    private User sender;

    @ManyToOne
    private Chat chat;

    // Getters and Setters
}


