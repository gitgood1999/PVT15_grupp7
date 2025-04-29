package com.example.demo;

import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

public class TomcatPingService {

    private final RestTemplate restTemplate = new RestTemplate();
    private final String tomcatUrl = "http://localhost:8080/tomcat";


    public boolean isTomcatUp() {
        try {
            ResponseEntity<String> response = restTemplate.getForEntity();
        } catch (Exception e) {
            System.out.println("Tomcat is not up" + e.getMessage());
            return false;
        }


    }

}