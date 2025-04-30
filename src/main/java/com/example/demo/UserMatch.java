package com.example.demo;

import jakarta.persistence.*;

@Entity
@Table(name = "user_match")
public class UserMatch {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private User user1;

    @ManyToOne
    private User user2;

    @OneToOne(mappedBy = "match", cascade = CascadeType.ALL)
    private Chat chat;

    public void setUser1(User user1) {
        this.user1 = user1;
    }
    public void setUser2(User user2) {
        this.user2 = user2;
    }
    public void setChat(Chat chat) {
        this.chat = chat;
    }
}


