package com.example.demo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

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

    public DataSeeder(CategoryRepository categoryRepository, UserController userController, AvailabilityService availabilityService, MatchRepository matchRepository, MatchService matchService, ChatRepository chatRepository) {
        this.categoryRepository = categoryRepository;
        this.userController = userController;
        this.availabilityService = availabilityService;
        this.matchRepository = matchRepository;
        this.matchService = matchService;
        this.chatRepository = chatRepository;
    }

    @Override
    public void run(String... args) throws Exception {

        // Create Messages

        //matchService.createMatch(userController.findUserById(50L),userController.findUserById(51L)); // matcha 2 användare med varandra via id





        // findUserMatch(userController.getUserRepository().findById(16L)); // findUserMatch funkar, returnerar den som väntat längst

       // findUserMatchList(userController.getUserRepository().findById(18L)); // metoden fungerar

        // userController.getUserRepository().setUserCategory(); // fungerar att ändra kategori

        // availabilityService.toggleAvailability(4L); // denna togglar användarens availability och timestampar, timestamp och availabilty tas bort vid toggle också

        // seedCategories(); //tar bort tidigare kategorier och lägg till alla kategorier som skapas i metoden

    }

    private void seedCategories() {
        categoryRepository.deleteAll();
        if (categoryRepository.findAll().isEmpty()) {
            categoryRepository.save(new Category("Study"));
            categoryRepository.save(new Category("Eat"));
            categoryRepository.save(new Category("Train"));
            categoryRepository.save(new Category("Whatever"));
            System.out.println("Default categories inserted.");
        }
    }




}
