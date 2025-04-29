package com.example.demo;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
public class Available {

    @Id
    private Long id; // ID delas med User

    @OneToOne
    @MapsId // använder samma ID som User
    @JoinColumn(name = "id") // FK och PK är samma kolumn
    private User user;

    @Column(nullable = false)
    private boolean available;

    private LocalDateTime availableSince;

    public Available() {}

    // skapa en ny Available och koppla till en User
    public Available(boolean available, LocalDateTime availableSince, User user) {
        this.available = available;
        this.availableSince = availableSince;
        this.user = user;
    }

    public Available(boolean available, User user) {
        this.available = available;
        this.user = user;
    }



    public Long getId() {
        return id;
    }

    // ingen setId() behövs eftersom id kommer från User via @MapsId

    public boolean isAvailable() {
        return available;
    }

    public void setAvailable(boolean available) {
        this.available = available;
    }

    public LocalDateTime getAvailableSince() {
        return availableSince;
    }

    public void setAvailableSince(LocalDateTime availableSince) {
        this.availableSince = availableSince;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
