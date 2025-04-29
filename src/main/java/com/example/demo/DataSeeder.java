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

    public DataSeeder(CategoryRepository categoryRepository, UserController userController, AvailabilityService availabilityService) {
        this.categoryRepository = categoryRepository;
        this.userController = userController;
        this.availabilityService = availabilityService;
    }

    @Override
    public void run(String... args) throws Exception {

       // findUserMatchList(userController.getUserRepository().findById(18L)); // metoden fungerar

//        availabilityService.toggleAvailability(4L);
//        availabilityService.toggleAvailability(6L);
//        availabilityService.toggleAvailability(7L);
//        availabilityService.toggleAvailability(10L);
//        availabilityService.toggleAvailability(11L);
//        availabilityService.toggleAvailability(13L);
//        availabilityService.toggleAvailability(14L);
//        availabilityService.toggleAvailability(15L);
//        availabilityService.toggleAvailability(16L);
//        availabilityService.toggleAvailability(17L);
//        availabilityService.toggleAvailability(18L);
//        availabilityService.toggleAvailability(19L);
//        availabilityService.toggleAvailability(20L);


//        userController.getUserRepository().setUserCategory(4L, categoryRepository.findByName("Whatever"));
//        userController.getUserRepository().setUserCategory(6L, categoryRepository.findByName("Study"));
//        userController.getUserRepository().setUserCategory(7L, categoryRepository.findByName("Whatever"));
//        userController.getUserRepository().setUserCategory(10L, categoryRepository.findByName("Train"));
//        userController.getUserRepository().setUserCategory(11L, categoryRepository.findByName("Whatever"));
//        userController.getUserRepository().setUserCategory(13L, categoryRepository.findByName("Eat"));
//        userController.getUserRepository().setUserCategory(14L, categoryRepository.findByName("Eat"));
//        userController.getUserRepository().setUserCategory(15L, categoryRepository.findByName("Eat"));
//        userController.getUserRepository().setUserCategory(16L, categoryRepository.findByName("Train"));
//        userController.getUserRepository().setUserCategory(17L, categoryRepository.findByName("Whatever"));
//        userController.getUserRepository().setUserCategory(18L, categoryRepository.findByName("Study"));
//        userController.getUserRepository().setUserCategory(19L, categoryRepository.findByName("Study"));
//        userController.getUserRepository().setUserCategory(20L, categoryRepository.findByName("Train"));


        // userRepository.setUserCategory(); // fungerar att ändra kategori

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


    public List<User> findUserMatchList(User user){
        if(userController.getUserRepository().findByEmail(user.getEmail())!=null){
            if(user.getCategory().getName().equals("Whatever")){
                return userController.getUserRepository().findAllExcludingUser(user.getId());
            }else{
                return userController.getUserRepository().findByCategoryOrWhateverAndAvailableTrueExcludingUser(user.getCategory().getName(),user.getId());
            }
        }else{
            return null;
        }
    }
}
