package com.example.demo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class DataSeeder implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(DataSeeder.class);
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final AvailabilityService availabilityService;
    private final AvailableRepository availableRepository;

    public DataSeeder(UserRepository userRepository,  CategoryRepository categoryRepository, AvailabilityService availabilityService, AvailableRepository availableRepository) {
        this.userRepository = userRepository;
        this.categoryRepository = categoryRepository;
        this.availabilityService = availabilityService;
        this.availableRepository = availableRepository;
    }

    @Override
    public void run(String... args) throws Exception {


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

    public boolean addUser(User user) {
        if (userRepository.findByEmail(user.getEmail()) != null){
            System.out.println("User already exists");
            return false;
        } else {
            userRepository.save(user);
            return true;
        }
    }

    public List<User> findUserMatchList(User user){
        if(checkIfUserExists(user)){
            if(user.getCategory().getName().equals("Whatever")){
                return userRepository.findAllExcludingUser(user.getId());
            }else{
                return userRepository.findByCategoryOrWhateverAndAvailableTrueExcludingUser(user.getCategory().getName(),user.getId());
            }
        }else{
            return null;
        }

    }

    public void removeUser(User user) {
        if (checkIfUserExists(user)){
            userRepository.delete(user);
        }
    }

    public boolean checkIfUserExists(User user) {
        return userRepository.findByEmail(user.getEmail()) != null;
    }

}
