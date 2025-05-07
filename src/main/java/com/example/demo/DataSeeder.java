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


    public DataSeeder(CategoryRepository categoryRepository, UserController userController, AvailabilityService availabilityService, MatchRepository matchRepository, MatchService matchService, ChatRepository chatRepository, MessageController messageController, UserRepository userRepository, MessageService messageService) {
        this.categoryRepository = categoryRepository;
        this.userController = userController;
        this.availabilityService = availabilityService;
        this.matchRepository = matchRepository;
        this.matchService = matchService;
        this.chatRepository = chatRepository;
        this.userRepository = userRepository;
        this.messageController= messageController;
    }

    @Override
    public void run(String... args) throws Exception {
       // seedCategories();
//        System.out.println(userController.authenticate("lösen",userRepository.findByEmail("hober@student.su.se").getPassword()));
//        User user = new User();
//        user.setName("Daniel");
//        user.setPassword("1234");
//        user.setEmail("danne@outlook.se");
//        userController.registerUser(user);
//        System.out.println(userController.authenticate("1234",userController.getUserRepository().findByEmail("danne@outlook.se").getPassword()));
//        System.out.println(userController.getUserRepository().findByEmail("danne@outlook.se").getPassword());
           // messageController.createMessage(new MessageDTO(29L, 50L, "does createMessage work"));



//        availabilityService.toggleAvailability(50L);
//        availabilityService.toggleAvailability(4L);
//
//        matchService.createMatch(userRepository.findById(50L), userRepository.findById(4L));
//        availabilityService.toggleAvailability(50L);
//        availabilityService.toggleAvailability(22L);
//        userRepository.setUserCategory(22L,categoryRepository.findByName("Whatever"));
//
//        matchService.createMatch(userRepository.findById(50L), userRepository.findById(22L));
//
//        availabilityService.toggleAvailability(50L);
//        availabilityService.toggleAvailability(28L);
//        userController.getUserRepository().setUserCategory(28L,categoryRepository.findByName("Whatever"));
//
//        matchService.createMatch(userRepository.findById(50L), userRepository.findById(28L));
//        availabilityService.toggleAvailability(50L);
//        availabilityService.toggleAvailability(27L);
//        userController.getUserRepository().setUserCategory(27L,categoryRepository.findByName("Whatever"));
//
//        matchService.createMatch(userRepository.findById(50L), userRepository.findById(27L));
//        availabilityService.toggleAvailability(50L);
//        availabilityService.toggleAvailability(26L);
//        userController.getUserRepository().setUserCategory(26L,categoryRepository.findByName("Whatever"));
//        matchService.createMatch(userRepository.findById(50L), userRepository.findById(26L));
//
//        availabilityService.toggleAvailability(50L);
//        availabilityService.toggleAvailability(25L);
//        userController.getUserRepository().setUserCategory(25L,categoryRepository.findByName("Whatever"));
//        matchService.createMatch(userRepository.findById(50L), userRepository.findById(25L));


        //Rensar alla matchningar och chattar samt tidigare matchningar
//        matchRepository.deleteAll();
//        chatRepository.deleteAll();
//        messageRepository.deleteAll();
//        userController.clearPreviousMatchesForAllUsers();

//        UserMatch match = matchService.getMatch(29L);
//
//
//        Message message3 = new Message();
//        message3.setSender(match.getUser1());
//        message3.setChat(match.getChat());
//        message3.setContent("lorem ipsum");
//        message3.setTimestamp(LocalDateTime.now());
//
//        Message message4 = new Message();
//        message4.setSender(match.getUser2());
//        message4.setChat(match.getChat());
//        message4.setContent("Lorem");
//        message4.setTimestamp(LocalDateTime.now());
//        messageRepository.save(message3);
//        messageRepository.save(message4);
//
//        // Persist messages
//        match.getChat().getMessages().add(message3);
//        match.getChat().getMessages().add(message4);
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
            categoryRepository.save(new Category("Grab A Fika"));
            categoryRepository.save(new Category("Study session"));
            categoryRepository.save(new Category("Go for a walk"));
            categoryRepository.save(new Category("Spontaneous fun"));
            System.out.println("Default categories inserted.");
        }
    }




}
