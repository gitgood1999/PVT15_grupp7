package com.example.demo;

import com.example.demo.User;
import com.example.demo.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class DataSeeder implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(DataSeeder.class);
    private final UserRepository userRepository;

    public DataSeeder(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public void run(String... args) throws Exception {

        if (userRepository.count() == 0) {
            userRepository.save(new User("Alice", "alice@example.com"));
            userRepository.save(new User("Bob", "bob@example.com"));
            userRepository.save(new User("Charlie", "charlie@example.com"));
            System.out.println("🌱 Exempelanvändare seedade!");
        } else {
            System.out.println("🙈 Användare finns redan – skippar seeding.");
        }

        userRepository.findAll().forEach(user -> System.out.println(user.getName()+" "+user.getEmail()));
    }
}
