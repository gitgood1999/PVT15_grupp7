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
        addUser(new User("Linus","Linus@gmail.com"));

        //User alice = userRepository.findByEmail("theo@example.com");
        //System.out.println(alice.getEmail());
        //userRepository.save(new User("Alice", "alice@example.com"));

        //System.out.println(userRepository.findById(1l));
        //userRepository.


        //userRepository.findAll().forEach(user -> System.out.println(user.getName()+" "+user.getEmail()));
    }
}
