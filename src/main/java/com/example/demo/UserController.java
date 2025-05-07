package com.example.demo;
import java.util.Optional;

import com.example.demo.User;
import com.example.demo.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;


import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("api/users")
public class UserController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private AvailabilityService availabilityService;

    @Autowired
    private MatchService matchService;

    @Autowired
    private NotificationService notificationService;
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


    //registrering av användare för första gången

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody User user) {

        // ✅ Check if user already exists
        if (userRepository.findByEmail(user.getEmail()) != null) {
            return ResponseEntity
                    .status(HttpStatus.CONFLICT)
                    .body(new ErrorResponse("User already exists"));
        }

        userRepository.setUserCategory(user.getId(), null);

        Available available = new Available(false, null, user);
        user.setAvailableStatus(available);

        String hashedPassword = passwordEncoder.encode(user.getPassword());
        user.setPassword(hashedPassword);

        User savedUser = userRepository.save(user);
        availabilityService.save(available);

        return ResponseEntity.ok(savedUser);
    }





    //metoden för att logga in
    @PostMapping("/login")
    public ResponseEntity<Object> loginUser(@RequestBody User loginData) {
        User user = userRepository.findByEmail(loginData.getEmail());

        if (user == null || !authenticate(loginData.getPassword(), user.getPassword())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("message", "Felaktig e-post eller lösenord"));
        }

        // returnera det man vill använda i Flutter
        return ResponseEntity.ok(Map.of(
                "id", user.getId(),
                "name", user.getName(),
                "email", user.getEmail(),
                "avatarIndex", user.getAvatarIndex()
                // man kan lägga till mer om dman behöver, tex categoryId eller availableStatus
        ));
    }
    public boolean authenticate(String rawPassword, String hashedPassword) {
        return passwordEncoder.matches(rawPassword, hashedPassword);
    }


    @PutMapping("/{id}/toggleAvailability")
    public ResponseEntity<Void> updateAvailability(@PathVariable Long id, @RequestBody Map<String, Object> availabilityData) {
        // hämta användaren från databasen
        User user = userRepository.findById(id).orElse(null);
        if (user == null) return ResponseEntity.notFound().build();

        // plockar ut värden från JSON-requesten
        boolean available = (Boolean) availabilityData.get("available");
        Integer totalMinutes = availabilityData.containsKey("totalMinutes")
                ? (Integer) availabilityData.get("totalMinutes")
                : null;

        Integer activityId = availabilityData.containsKey("activityId")
                ? (Integer) availabilityData.get("activityId")
                : null;

        // hämtar eller skapar Available-objektet
        Available status = user.getAvailableStatus();
        if (status == null) {
            status = new Available();
            status.setUser(user);
        }

        status.setAvailable(available);

        if (available) {
            LocalDateTime now = LocalDateTime.now();
            status.setAvailableSince(now);
            status.setAvailableUntil(totalMinutes != null ? now.plusMinutes(totalMinutes) : null);
        } else {
            status.setAvailableSince(null);
            status.setAvailableUntil(null);
        }

        // om användaren har valt en kategori, koppla den till user
        if (activityId != null) {
            Category category = categoryRepository.findById(activityId.longValue());
            user.setCategory(category); // här sätter vi aktiviteten direkt på användaren
        }

        // spara ändringar
        user.setAvailableStatus(status);
        userRepository.save(user);

        return ResponseEntity.ok().build();
    }



    @GetMapping("/{id}")
    public ResponseEntity<User> getUserWithAvailability(@PathVariable Long id) {
        return userRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }






    //hjälpmetod till findUserMatch
    private List<User> findUserMatchList(User user){
        if(userRepository.findByEmail(user.getEmail())!=null){
            if(user.getCategory().getName().equals("Spontaneous fun")){
                return userRepository.findAllExcludingUser(user.getId());
            }else{
                return userRepository.findByCategoryOrWhateverAndAvailableTrueExcludingUser(user.getCategory().getName(),user.getId(), user.getPreviousMatches());
            }
        }else{
            return null;
        }
    }

    @GetMapping("/match")
    public User findUserMatch(@RequestBody User user) {
        List<User> matchList = findUserMatchList(user);
        if (matchList == null || matchList.isEmpty()) {
            return null;
        }
        return matchList.stream()
                .filter(u -> u.getAvailableStatus() != null && u.getAvailableStatus().getAvailableSince() != null)
                .min(Comparator.comparing(u -> u.getAvailableStatus().getAvailableSince()))
                .orElse(null);
    }
    @PostMapping
    public boolean matchUser(User user) {
        User user2 = findUserMatch(user);

        if (user2 == null) {
            return false;
        }
        matchService.createMatch(user, user2);
        return true;
    }
    public User findUserById(@PathVariable Long id) {
        return userRepository.findById(id).orElse(null);
    }

    public UserRepository getUserRepository() {
        return userRepository;
    }

    @Transactional
    public void clearPreviousMatchesForAllUsers() {
        List<User> allUsers = userRepository.findAll();
        for (User user : allUsers) {
            user.getPreviousMatches().clear();
        }
        userRepository.saveAll(allUsers);
    }


    //metod för att uppdatera avatar via settings
    @PutMapping("/updateAvatar")
    public ResponseEntity<?> updateAvatar(@RequestBody Map<String, Object> data) {
        String email = (String) data.get("email");
        Integer avatarIndex = (Integer) data.get("avatarIndex");

        User user = userRepository.findByEmail(email);
        if (user == null) return ResponseEntity.notFound().build();

        user.setAvatarIndex(avatarIndex);
        userRepository.save(user);


        //TEST FUNKTION FÖR WEBSOCKET
        notificationService.sendDatabaseChangeNotification("User " + user.getId() + " updated avatar to index " + avatarIndex, user.getEmail());


        return ResponseEntity.ok().build();

    }


    //metod för uppdatering av användarnamn via settings
    @PutMapping("/updateName")
    public ResponseEntity<?> updateName(@RequestBody Map<String, String> data) {
        String email = data.get("email");
        String newName = data.get("name");

        User user = userRepository.findByEmail(email);
        if (user == null) {
            return ResponseEntity.notFound().build();
        }

        user.setName(newName);
        userRepository.save(user);

        return ResponseEntity.ok().build();
    }











}
