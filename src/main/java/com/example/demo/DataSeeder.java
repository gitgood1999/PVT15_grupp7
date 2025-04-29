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

    public DataSeeder(CategoryRepository categoryRepository, UserController userController, AvailabilityService availabilityService) {
        this.categoryRepository = categoryRepository;
        this.userController = userController;
        this.availabilityService = availabilityService;
    }

    @Override
    public void run(String... args) throws Exception {

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

    public User findUserMatch(User user) {
        List<User> matchList = findUserMatchList(user);
        if (matchList == null || matchList.isEmpty()) {
            return null;
        }
        return matchList.stream()
                .filter(u -> u.getAvailableStatus() != null && u.getAvailableStatus().getAvailableSince() != null)
                .min(Comparator.comparing(u -> u.getAvailableStatus().getAvailableSince()))
                .orElse(null);
    }

}
