package com.example.demo;

import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

public class TomcatPingService {

    private final RestTemplate restTemplate = new RestTemplate();


    public boolean isTomcatUp() {
        try {
            String tomcatUrl = "http://localhost:8080/tomcat";
            ResponseEntity<String> response = restTemplate.getForEntity(tomcatUrl, String.class);
        } catch (Exception e) {
            System.out.println("Tomcat is not up" + e.getMessage());
            return false;
        }


    }

}