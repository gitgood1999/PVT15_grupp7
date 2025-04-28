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
    private final CategoryRepository categoryRepository;

    public DataSeeder(UserRepository userRepository,  CategoryRepository categoryRepository) {
        this.userRepository = userRepository;
        this.categoryRepository = categoryRepository;
    }

    @Override
    public void run(String... args) throws Exception {
        System.out.println(userRepository.findByAvailableTrueAndCategory(categoryRepository.findByName("Study")));

       // user.setCategory(category);
       //user2.setCategory(category);
       // userRepository.toggleAvailableById(user.getId());
        //userRepository.toggleAvailableById(user2.getId());

        //seedCategories();
        //userRepository.toggleAvailableById(1);
        //System.out.println(userRepository.findByAvailableTrue());


        //User alice = userRepository.findByEmail("theo@example.com");
        //System.out.println(alice.getEmail());
        //userRepository.save(new User("Alice", "alice@example.com"));

        //System.out.println(userRepository.findById(1l));
        //userRepository.


        //userRepository.findAll().forEach(user -> System.out.println(user.getName()+" "+user.getEmail()));
    }

    private void seedCategories() {
        if (categoryRepository.findAll().isEmpty()) {
            categoryRepository.save(new Category("Study"));
            categoryRepository.save(new Category("Eat"));
            categoryRepository.save(new Category("Train"));
            categoryRepository.save(new Category("Whatever"));
            System.out.println("Default categories inserted.");
        }
    }

    public void addUser(User user) {

        if (userRepository.findByEmail(user.getEmail()) != null){
            System.out.println("User already exists");
        } else {
            userRepository.save(user);
        }
    }

    public boolean findUserWithMatchingCategory(User user){

        //Hallå
        return false;
    }

    public void removeUser(User user) {

        if (checkIfUserExists(user)){
            userRepository.delete(user);
        }


    }

    public boolean checkIfUserExists(User user) {

        if (userRepository.findByEmail(user.getEmail()) == null){
            return false;
        }
        return true;
    }

}
