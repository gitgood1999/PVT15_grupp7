package com.example.demo;
import java.util.Optional;

import com.example.demo.User;
import com.example.demo.UserRepository;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.messaging.FirebaseMessagingException;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;



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
    private FCMService fcmService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private AvailabilityService availabilityService;

    @Autowired
    private MatchService matchService;




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
    public ResponseEntity<Object> registerUser(@RequestBody User user) {
        String email = user.getEmail();
        if (!email.matches("^[\\w.-]+@student\\.su\\.se$")) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(new ErrorResponse("Email must be a valid student.su.se address"));
        }
        if (userRepository.findByEmail(email) != null) {
            return ResponseEntity
                    .status(HttpStatus.CONFLICT)
                    .body(new ErrorResponse("User already exists"));
        }

        // Hasha lösenord och spara user i databasen
        String hashedPassword = passwordEncoder.encode(user.getPassword());
        user.setPassword(hashedPassword);
        Available available = new Available(false, null, user);
        user.setAvailableStatus(available);
        User savedUser = userRepository.save(user);
        availabilityService.save(available);

        try {
            // Skapa custom token för den nyregistrerade användaren
            // Före:

           String firebaseToken = FirebaseAuth.getInstance()
                   .createCustomToken(String.valueOf(savedUser.getId()));


            // Returnera user + token
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(Map.of(
                            "id", savedUser.getId(),
                            "name", savedUser.getName(),
                            "email", savedUser.getEmail(),
                            "avatarIndex", savedUser.getAvatarIndex(),
                            "firebaseToken", firebaseToken
                    ));
        } catch (FirebaseAuthException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", "Could not create Firebase token"));
        }
    }







    @PostMapping("/login")
    public ResponseEntity<Object> loginUser(@RequestBody User loginData) {
        User user = userRepository.findByEmail(loginData.getEmail());
        if (user == null || !authenticate(loginData.getPassword(), user.getPassword())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("message", "Felaktig e-post eller lösenord"));
        }

        try {
            // Skapa custom token baserat på din interna user.id (omvandla till sträng)
            String firebaseToken = FirebaseAuth.getInstance()
                    .createCustomToken(String.valueOf(user.getId()));

            // Returnera token tillsammans med dina övriga fält
            return ResponseEntity.ok(Map.of(
                    "id", user.getId(),
                    "name", user.getName(),
                    "email", user.getEmail(),
                    "avatarIndex", user.getAvatarIndex(),
                    "firebaseToken", firebaseToken
            ));
        } catch (FirebaseAuthException e) {
            // Hantera fel vid token-skapande
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", "Could not create Firebase token"));
        }
    }




    @PutMapping("/{id}/toggleAvailability")
    public ResponseEntity<Void> updateAvailability(
            @PathVariable Long id,
            @RequestBody Map<String, Object> availabilityData
    ) {
        System.out.println(">>> Received availability update for userId=" + id + ": " + availabilityData);

        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        // Hämta eller skapa ett Available‐objekt
        Available status = Optional.ofNullable(user.getAvailableStatus())
                .orElseGet(() -> {
                    Available a = new Available();
                    a.setUser(user);
                    return a;
                });

        // Robust tolkning av "available"
        Object availableObj = availabilityData.get("available");
        boolean available = (availableObj instanceof Boolean)
                ? (Boolean) availableObj
                : Boolean.parseBoolean(String.valueOf(availableObj));

        Integer totalMinutes = (Integer) availabilityData.getOrDefault("totalMinutes", null);

        status.setAvailable(available);

        if (available) {
            LocalDateTime start;
            if (availabilityData.containsKey("from")) {
                start = LocalDateTime.parse((String) availabilityData.get("from"));
            } else {
                start = LocalDateTime.now();
            }
            status.setAvailableSince(start);
            status.setAvailableUntil(totalMinutes != null ? start.plusMinutes(totalMinutes) : null);
        } else {
            status.setAvailableSince(null);
            status.setAvailableUntil(null);
        }

        user.setAvailableStatus(status);
        availabilityService.save(status);


        // Kategori (activityId)
        if (availabilityData.containsKey("activityId")) {
            Integer activityId = (Integer) availabilityData.get("activityId");
            if (activityId != null) {
                Category category = categoryRepository.findById(Long.valueOf(activityId))
                        .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Category not found"));
                user.setCategory(category);
            }
        }

        userRepository.save(user);

        // Matchningslogik
        if (available) {
            matchService.findAvailableMatchForUser(id)
                    .ifPresent(partner -> matchService.createMatch(user, partner));
        }

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
                return userRepository.findByCategoryOrSpontaneousAndAvailableTrueExcludingUser(user.getCategory().getName(),user.getId(), user.getPreviousMatches());
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

    @PutMapping("/{id}/fcm-token")
    public ResponseEntity<?> updateFcmToken(@PathVariable Long id, @RequestBody Map<String, String> body) {
        String token = body.get("token");

        User user = userRepository.findById(id).orElse(null);
        if (user == null) {
            return ResponseEntity.notFound().build();
        }

        user.setFcmToken(token);
        userRepository.save(user);
        return ResponseEntity.ok().build();
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
        /*
        try {
            if (user.getFcmToken() != null) {
                fcmService.sendMessage(
                        user.getFcmToken(),
                        "Avatar updated",
                        "Your profile avatar was successfully updated."
                );
            }
        } catch (FirebaseMessagingException e) {
            e.printStackTrace(); // Optionally handle error better
        }

        */


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




    @GetMapping("/{id}/match")
    public ResponseEntity<MatchResponse> findUserMatchById(@PathVariable Long id) {
        return matchService.findAvailableMatchForUser(id)
                .map(partner -> {

                    MatchResponse resp = new MatchResponse(
                            partner.getId(),
                            partner.getName(),
                            partner.getAvatarIndex()
                    );
                    return ResponseEntity.ok(resp);
                })
                .orElseGet(() -> ResponseEntity.noContent().build());
    }





    private boolean authenticate(String rawPassword, String hashedPassword) {
        return passwordEncoder.matches(rawPassword, hashedPassword);
    }







}
