package com.example.demo;

import com.example.demo.User;
import com.example.demo.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/users")
public class UserController {

    @Autowired
    private UserRepository userRepository;

    // Hämta alla användare
    @GetMapping
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    // Lägg till en ny användare
    @PostMapping
    public User createUser(@RequestBody User user) {
        return userRepository.save(user);
    }


}
