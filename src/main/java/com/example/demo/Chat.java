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
    @JoinColumn(name = "match_id", nullable = false)
    private UserMatch match;

    @OneToMany(mappedBy = "chat", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Message> messages = new ArrayList<>();

    public Long getId() {
        return id;
    }

    public UserMatch getMatch() {
        return match;
    }

    public void setMatch(UserMatch match) {
        this.match = match;
    }

    public List<Message> getMessages() {
        return messages;
    }

    public void addMessage(Message message){
        messages.add(message);
        message.setChat(this);
    }

    public void removeMessage(Message message){
        messages.remove(message);
        message.setChat(null);
    }

}

