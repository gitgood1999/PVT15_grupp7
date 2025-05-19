package com.example.demo;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;


@Entity
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @Column(unique = true)
    private String email;

    private String password;

    @Column(name = "avatar_index")
    private Integer avatarIndex = 0;

    @ManyToOne
    @JoinColumn(name = "category_id")
    private Category category;


    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private Available availableStatus; // Inget @PrimaryKeyJoinColumn här!

    // Previous matches
    @ManyToMany
    @JoinTable(
            name = "user_previous_matches",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "previous_user_id")
    )
    private List<User> previousMatches = new ArrayList<>();

    // Standardkonstruktor
    public User() {}

    public List<User> getPreviousMatches() {
        return previousMatches;
    }

    private String fcmToken;

    public Integer getAvatarIndex() {
        return avatarIndex;
    }

    public void setAvatarIndex(Integer avatarIndex) {
        this.avatarIndex = avatarIndex;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password=password;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public Available getAvailableStatus() {return availableStatus;}

    public String toString(){ return name;}

    public void setAvailableStatus(Available availableStatus) {
        this.availableStatus = availableStatus;
    }

    public String getFcmToken() {
        return fcmToken;
    }

    public void setFcmToken(String fcmToken) {
        this.fcmToken = fcmToken;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof User)) return false;
        User that = (User) o;
        return id != null && id.equals(that.id);
    }


    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }
}
