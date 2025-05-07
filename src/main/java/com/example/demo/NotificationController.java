package com.example.demo;

import com.google.firebase.messaging.FirebaseMessagingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/notify")
public class NotificationController {

    @Autowired
    private FCMService fcmService;

    @PostMapping
    public ResponseEntity<String> sendNotification(@RequestParam String token) {
        try {
            String response = fcmService.sendMessage(token, "Hello", "This is a test push");
            return ResponseEntity.ok("Message sent: " + response);
        } catch (FirebaseMessagingException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error: " + e.getMessage());
        }
    }
}