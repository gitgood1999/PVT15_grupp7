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
    public Long getId() {
        return id;
    }
    public User getUser1() {
        return user1;
    }
    public User getUser2() {
        return user2;
    }

    @Override
    public boolean equals(Object o) {
        if(this == o) return true;
        if(o == null || getClass() != o.getClass()) return false;
        UserMatch userMatch = (UserMatch) o;
        return id.equals(userMatch.id);
    }

    public String toString() {
        return user1.getEmail() + " " + user2.getEmail();
    }
}


