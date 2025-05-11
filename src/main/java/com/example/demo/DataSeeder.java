package com.example.demo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;

@Component
public class DataSeeder implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(DataSeeder.class);
    private final CategoryRepository categoryRepository;
    private final UserController userController;
    private final AvailabilityService availabilityService;
    private final MatchRepository matchRepository;
    private final MatchService matchService;
    private final ChatRepository chatRepository;
    private final MessageController messageController;
    private final UserRepository userRepository;
    private final UserMatchRepository userMatchRepository;


    public DataSeeder(CategoryRepository categoryRepository, UserController userController, AvailabilityService availabilityService, MatchRepository matchRepository, MatchService matchService, ChatRepository chatRepository, MessageController messageController, UserRepository userRepository, MessageService messageService, AvailableRepository availableRepository, UserMatchRepository userMatchRepository) {
        this.categoryRepository = categoryRepository;
        this.userController = userController;
        this.availabilityService = availabilityService;
        this.matchRepository = matchRepository;
        this.matchService = matchService;
        this.chatRepository = chatRepository;
        this.userRepository = userRepository;
        this.messageController= messageController;
        this.userMatchRepository = userMatchRepository;
    }

    @Override
    public void run(String... args) throws Exception {

        //Rensar alla matchningar och chattar samt tidigare matchningar
//        matchRepository.deleteAll();
//        chatRepository.deleteAll();
//        messageRepository.deleteAll();
//        userController.clearPreviousMatchesForAllUsers();

        // seedCategories(); //tar bort tidigare kategorier och lägg till alla kategorier som skapas i metoden

    }

    private void seedCategories() {
        categoryRepository.deleteAll();
        if (categoryRepository.findAll().isEmpty()) {
            categoryRepository.save(new Category("Grab A Fika"));
            categoryRepository.save(new Category("Study session"));
            categoryRepository.save(new Category("Go for a walk"));
            categoryRepository.save(new Category("Spontaneous fun"));
            System.out.println("Default categories inserted.");
        }
    }




}
