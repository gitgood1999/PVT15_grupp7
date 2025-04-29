package com.example.demo;

import com.example.demo.User;
import com.example.demo.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("api/users")
public class UserController {

    @Autowired
    private UserRepository userRepository;

    // Hämta alla användare
    @GetMapping
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    // Ta bort en användare
    @DeleteMapping("/{id}")
    public void deleteUser(@PathVariable Long id) {
        userRepository.deleteById(id);
    }

    //registrering
    @PostMapping("/register")
    public ResponseEntity<User> registerUser(@RequestBody User user) {
        if (userRepository.findByEmail(user.getEmail()) != null) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build(); // Användare finns redan
        }
        User savedUser = userRepository.save(user);
        return ResponseEntity.ok(savedUser);
    }

    @PostMapping("/login")
    public ResponseEntity<User> loginUser(@RequestBody User loginData) {
        User user = userRepository.findByEmail(loginData.getEmail());
        if (user == null || !user.getPassword().equals(loginData.getPassword())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build(); //fel login
        }
        return ResponseEntity.ok(user); //rätt login, returna användaren
    }

    public UserRepository getUserRepository() {
        return userRepository;
    }





}
