package com.example.demo;

import com.example.demo.User;
import com.example.demo.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("api/users")
public class UserController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    // hämta alla användare

    @GetMapping
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    //ta bort en användare

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

        //kolla eller skapa standardkategori

        Category defaultCategory = categoryRepository.findByName("Standard Category");
        if (defaultCategory == null) {
            defaultCategory = new Category("Standard Category");
            categoryRepository.save(defaultCategory);
        }
        user.setCategory(defaultCategory);

        // spara användaren först så vi får ID

        User savedUser = userRepository.save(user);

        //skapa Available och koppla till sparade användaren

        Available available = new Available(false, null, savedUser);
        savedUser.setAvailableStatus(available);

        savedUser = userRepository.save(savedUser);

        return ResponseEntity.ok(savedUser);
    }





    //login
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
