package com.example.demo;

import com.example.demo.User;
import com.example.demo.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
        Available available = new Available(false, null, user);
        user.setAvailableStatus(available);

        User savedUser = userRepository.save(user);

// spara först användaren
        savedUser = userRepository.save(savedUser);

// sen spara available separat också, för säkerhets skull
        availabilityService.save(available);

        return ResponseEntity.ok(savedUser);

    }




    //metoden för att logga in
    @PostMapping("/login")
    public ResponseEntity<Object> loginUser(@RequestBody User loginData) {
        User user = userRepository.findByEmail(loginData.getEmail());

        if (user == null || !user.getPassword().equals(loginData.getPassword())) {
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


    @PutMapping("/{id}/toggleAvailability")
    public ResponseEntity<Void> updateAvailability(@PathVariable Long id, @RequestBody Map<String, Boolean> availabilityData) {
        // hämta användaren från databasen
        User user = userRepository.findById(id).orElse(null);
        if (user == null) return ResponseEntity.notFound().build();

        
        boolean available = availabilityData.get("available");

        // uppdatera availability och sätt timestamp om true, annars nollställ
        user.getAvailableStatus().setAvailable(available);
        user.getAvailableStatus().setAvailableSince(available ? LocalDateTime.now() : null);

        // spara ändringen till databasen
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
            if(user.getCategory().getName().equals("Whatever")){
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
        notificationService.sendDatabaseChangeNotification(
                "User " + user.getId() + " updated avatar to index " + avatarIndex
        );


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
