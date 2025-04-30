package com.example.demo;

import jakarta.persistence.*;
import java.util.List;
import java.util.ArrayList;

@Entity
public class Chat {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "match_id")
    private UserMatch match;

    @OneToMany(mappedBy = "chat", cascade = CascadeType.ALL)
    private List<Message> messages = new ArrayList<>();

    public void setMatch(UserMatch match) {
        this.match = match;
    }
}

