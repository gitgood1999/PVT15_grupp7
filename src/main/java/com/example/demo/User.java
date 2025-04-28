package com.example.demo;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    private String name;
    private String email;
    private String password;
    private String category;
    private boolean available;

    public User() {

    }

    public User(String name, String email) {
        this.name = name;
        this.email = email;
        this.available = false;
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

    public void setPassword() {
        this.password=password;
    }

    public String getCategory() {
        return category;
    }

    public boolean isAvailable() {return available; }

    public void setCategory(String category) {
        this.category = category;
    }

    public void toggleAvailable() {this.available = !this.available;}

    public String toString(){ return name;}


}
