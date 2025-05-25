package com.example.demo;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.auth.oauth2.ServiceAccountCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.io.InputStream;

@Component
public class FirebaseConfig {
    private static final Logger logger = LoggerFactory.getLogger(FirebaseConfig.class);

    @PostConstruct             // <- här
    public void initialize() throws Exception {
        InputStream is = new ClassPathResource("firebase-service-account.json")
                .getInputStream();

        ServiceAccountCredentials creds =
                (ServiceAccountCredentials) GoogleCredentials.fromStream(is);

        logger.info("🔑 Loaded Firebase service account: {}", creds.getClientEmail());

        FirebaseOptions options = FirebaseOptions.builder()
                .setCredentials(creds)
                .build();

        if (FirebaseApp.getApps().isEmpty()) {
            FirebaseApp.initializeApp(options);
            logger.info("✅ FirebaseApp initialized.");
        }
    }
}
