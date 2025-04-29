package com.example.demo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class DataSeeder implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(DataSeeder.class);
    private final CategoryRepository categoryRepository;
    private final UserController userController;
    private final AvailabilityService availabilityService;
    private final AvailableRepository availableRepository;

    public DataSeeder(CategoryRepository categoryRepository, UserController userController, AvailabilityService availabilityService, AvailableRepository availableRepository) {
        this.categoryRepository = categoryRepository;
        this.userController = userController;
        this.availabilityService = availabilityService;
        this.availableRepository = availableRepository;
    }

    @Override
    public void run(String... args) throws Exception {
        
        System.out.println(userController.getUserRepository().findAllExcludingUser(19L));
        // userRepository.setUserCategory(); // fungerar att ändra kategori

        // availabilityService.toggleAvailability(4L); // denna togglar användarens availability och timestampar, timestamp och availabilty tas bort vid toggle också

        // addUser(user); för att lägga till användare

        //findUserMatchList(User user); // returnerar lista med potentiella matchningar där användaren som skickas med exkluderas

        // removeUser(user); // ta bort specifik användare

        // seedCategories(); //tar bort tidigare kategorier och lägg till alla kategorier som skapas i metoden

       //checkIfUserExists(user) // kolla om användare finns

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


//    public List<User> findUserMatchList(User user){
//        if(checkIfUserExists(user)){
//            if(user.getCategory().getName().equals("Whatever")){
//                return userRepository.findAllExcludingUser(user.getId());
//            }else{
//                return userRepository.findByCategoryOrWhateverAndAvailableTrueExcludingUser(user.getCategory().getName(),user.getId());
//            }
//        }else{
//            return null;
//        }
//    }
}
