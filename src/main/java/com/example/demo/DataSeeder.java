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
    private final MessageRepository messageRepository;
    private final UserRepository userRepository;

    public DataSeeder(CategoryRepository categoryRepository, UserController userController, AvailabilityService availabilityService, MatchRepository matchRepository, MatchService matchService, ChatRepository chatRepository, MessageRepository messageRepository, UserRepository userRepository) {
        this.categoryRepository = categoryRepository;
        this.userController = userController;
        this.availabilityService = availabilityService;
        this.matchRepository = matchRepository;
        this.matchService = matchService;
        this.chatRepository = chatRepository;
        this.messageRepository = messageRepository;
        this.userRepository = userRepository;
    }

    @Override
    public void run(String... args) throws Exception {

        UserMatch match = matchService.createMatch(userRepository.findById(50L),userRepository.findById(51L));
        //NEDANSTÅENDE KODBLOCK LÄGGER TILL MEDDELANDEN I MESSAGE SOM ÄR KOPPLADE TILL CHAT OCH DÄRMED USERMATCH
        Message message1 = new Message();
        message1.setSender(match.getUser1());
        message1.setChat(match.getChat());
        message1.setContent("Hello from user1");
        message1.setTimestamp(LocalDateTime.now());

        Message message2 = new Message();
        message2.setSender(match.getUser2());
        message2.setChat(match.getChat());
        message2.setContent("Hi there from user2");
        message2.setTimestamp(LocalDateTime.now());
        messageRepository.save(message1);
        messageRepository.save(message2);
//
//        // Persist messages
//        match.getChat().getMessages().add(message1);
//        match.getChat().getMessages().add(message2);
//        chatRepository.save(match.getChat());



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
