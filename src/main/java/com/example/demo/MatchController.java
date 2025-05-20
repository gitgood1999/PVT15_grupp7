package com.example.demo;

import com.example.demo.MatchService;
import com.example.demo.User;
import com.example.demo.UserMatch;
import com.example.demo.UserRepository;
import com.example.demo.ErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/match")
public class MatchController {

    private final MatchService   matchService;
    private final UserRepository userRepository;

    public MatchController(MatchService matchService,
                           UserRepository userRepository) {
        this.matchService   = matchService;
        this.userRepository = userRepository;
    }

    /**
     * Hämta en potentiell match för användaren (t.ex. direktkörning när
     * man blir available).
     */
    @GetMapping("/findmatch/{userId}")
    public ResponseEntity<User> findMatch(@PathVariable Long userId) {
        return matchService.findAvailableMatchForUser(userId)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.noContent().build());
    }

    /**
     * Polling‐endpoint för Flutter: returnerar match när den skapas.
     */
    @GetMapping("/poll/{userId}")
    public ResponseEntity<Map<String,Object>> pollMatch(@PathVariable Long userId) {
        Optional<UserMatch> opt = matchService.findMatchForUser(userId);
        if (opt.isEmpty()) {
            return ResponseEntity.noContent().build();
        }

        UserMatch  m           = opt.get();
        User       partner     = (m.getUser1().getId()==(userId))
                ? m.getUser2()
                : m.getUser1();
        Map<String,Object> body = new HashMap<>();
        body.put("name",        partner.getName());
        body.put("avatarIndex", partner.getAvatarIndex());
        body.put("matchId",     m.getId());

        return ResponseEntity.ok(body);
    }

    /**
     * Raderar en match + backar ut användarna ur varandras previousMatches.
     */
    @DeleteMapping("/{matchId}")
    @Transactional
    public ResponseEntity<?> deleteMatch(@PathVariable Long matchId) {
        Optional<UserMatch> opt = matchService.getMatch(matchId);
        if (opt.isEmpty()) {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(new ErrorResponse("Match not found"));
        }

        // 1) Hämta
        UserMatch m  = opt.get();
        User      u1 = m.getUser1();
        User      u2 = m.getUser2();

        // 2) Ta bort varandra ur previousMatches
        u1.getPreviousMatches().remove(u2);
        u2.getPreviousMatches().remove(u1);

        // 3) Spara användarna
        userRepository.save(u1);
        userRepository.save(u2);

        // 4) Radera själva match‐entiteten
        matchService.deleteMatch(matchId);

        return ResponseEntity.ok().build();
    }



    @DeleteMapping("/chat/{matchId}")
    @Transactional
    public ResponseEntity<?> deleteChatOnly(@PathVariable Long matchId) {
        Optional<UserMatch> opt = matchService.getMatch(matchId);
        if (opt.isEmpty()) {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(new ErrorResponse("Match not found"));
        }

        matchService.deleteMatch(matchId);
        return ResponseEntity.ok().build();
    }



}
