package com.example.demo;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
public class Available {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private boolean available;

    private LocalDateTime availableSince;

    @OneToOne
    @JoinColumn(name = "user_id", unique = true)
    private User user;

    public Available() {}

    public Available(boolean available, LocalDateTime availableSince, User user) {
        this.available = available;
        this.availableSince = availableSince;
        this.user = user;
    }

    public Long getId() {
        return id;
    }

    public LocalDateTime getAvailableSince() {
        return availableSince;
    }

    public boolean isAvailable() {
        return available;
    }

    public void setAvailable(boolean newStatus) {
        this.available = newStatus;
    }

    public void setAvailableSince(LocalDateTime now) {
        this.availableSince = now;
    }
}
