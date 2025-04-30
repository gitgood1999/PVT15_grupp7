package com.example.demo;

import jakarta.persistence.*;

@Entity
public class Match {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private User user1;

    @ManyToOne
    private User user2;

    @OneToOne(mappedBy = "match", cascade = CascadeType.ALL)
    private Chat chat;
}


