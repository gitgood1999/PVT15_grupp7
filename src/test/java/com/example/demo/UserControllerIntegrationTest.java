package com.example.demo;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.util.AssertionErrors.assertEquals;

@SpringBootTest
@Transactional
@AutoConfigureMockMvc // Optional: If you need to mock HTTP requests for integration tests
public class UserControllerIntegrationTest {

    @Autowired
    private UserController userController;

    @Autowired
    private MessageController messageController;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserMatchRepository userMatchRepository;

    @Autowired
    private ChatRepository chatRepository;

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private CategoryRepository categoryRepository;


    @Test
    public void testRegisterUser() {
        // Given
        User user = new User();
        user.setName("IntegrationTestName");
        user.setEmail("IntegrationTestEmail@student.su.se");
        user.setPassword("IntegrationTestPassword");

        ResponseEntity<?> response = userController.registerUser(user);

        assertEquals("Expected status code to be CREATED (201) after successful user registration",HttpStatus.CREATED, response.getStatusCode());

        User registeredUser = userRepository.findByName("IntegrationTestName");

        assertNotNull(registeredUser);
        assertEquals("User email in database not correct","IntegrationTestEmail@student.su.se", registeredUser.getEmail());
    }


    @Test
    public void testUpdateAvailability() {
        // Given
        User user = new User();
        user.setName("IntegrationTestName");
        user.setEmail("IntegrationTestEmail@student.su.se");
        user.setPassword("IntegrationTestPassword");
        userController.registerUser(user);

        Category category = categoryRepository.findByName("Go for a walk");

        Map<String, Object> availabilityData = new HashMap<>();
        availabilityData.put("available", true);
        availabilityData.put("totalMinutes", 30);
        availabilityData.put("activityId", category.getId().intValue());

        ResponseEntity<Void> response = userController.updateAvailability(user.getId(), availabilityData);

        assertEquals("Expected status code is OK",HttpStatus.OK, response.getStatusCode());

        User updatedUser = userRepository.findByName(user.getName());
        Available status = updatedUser.getAvailableStatus();

        assertTrue(status.isAvailable(), "User is not available, but should be");
        assertNotNull(status.getAvailableSince(),"Available Since not found");
        assertNotNull(status.getAvailableUntil(),"Available Until not found");

        assertEquals("CategoryID is not 1 as expected",category.getId(),updatedUser.getCategory().getId());


    }

    @Test
    public void testCreateMatchAndChat() {
        // Given
        User user1 = new User();
        user1.setName("IntegrationTestName");
        user1.setEmail("IntegrationTestEmail@student.su.se");
        user1.setPassword("IntegrationTestPassword");
        userController.registerUser(user1);

        User user2 = new User();
        user2.setName("IntegrationTestName2");
        user2.setEmail("IntegrationTestEmail2@student.su.se");
        user2.setPassword("IntegrationTestPassword2");
        userController.registerUser(user2);

        User savedUser1 = userRepository.findByName("IntegrationTestName");
        User savedUser2 = userRepository.findByName("IntegrationTestName2");

        Category category = categoryRepository.findByName("Go for a walk");

        Map<String, Object> availabilityData = new HashMap<>();
        availabilityData.put("available", true);
        availabilityData.put("totalMinutes", 30);
        availabilityData.put("activityId", category.getId().intValue());

        userController.updateAvailability(savedUser1.getId(), availabilityData);
        userController.updateAvailability(savedUser2.getId(), availabilityData);



        boolean isMatch = userController.matchUser(user1);
        assertTrue(isMatch, "User is not matching");

        UserMatch match = userMatchRepository.findByUsers(savedUser1, savedUser2);
        assertNotNull(match, "User match not found between user1 and user2");

        Chat chat = chatRepository.findByMatch(match);
        assertNotNull(chat, "Expected Chat to be linked to UserMatch");
    }

    @Test
    public void testSendMessage() {
        User user1 = new User();
        user1.setName("IntegrationTestName");
        user1.setEmail("IntegrationTestEmail@student.su.se");
        user1.setPassword("IntegrationTestPassword");
        userController.registerUser(user1);

        User user2 = new User();
        user2.setName("IntegrationTestName2");
        user2.setEmail("IntegrationTestEmail2@student.su.se");
        user2.setPassword("IntegrationTestPassword2");
        userController.registerUser(user2);

        User savedUser1 = userRepository.findByName("IntegrationTestName");
        User savedUser2 = userRepository.findByName("IntegrationTestName2");

        Category category = categoryRepository.findByName("Go for a walk");

        Map<String, Object> availabilityData = new HashMap<>();
        availabilityData.put("available", true);
        availabilityData.put("totalMinutes", 30);
        availabilityData.put("activityId", category.getId().intValue());

        userController.updateAvailability(savedUser1.getId(), availabilityData);
        userController.updateAvailability(savedUser2.getId(), availabilityData);

        boolean isMatch = userController.matchUser(savedUser1);
        assertTrue(isMatch);

        UserMatch match = userMatchRepository.findByUsers(savedUser1, savedUser2);
        assertNotNull(match, "User match not found between user1 and user2");

        Chat chat = chatRepository.findByMatch(match);
        assertNotNull(chat, "Expected Chat to be linked to UserMatch");

        // When: User1 sends a message
        MessageDTO message1 = new MessageDTO(chat.getId(), savedUser1.getId(), "Hello from IntegrationTestName");
        ResponseEntity<Message> response = messageController.createMessage(message1);

        // Then
        assertEquals("Expected responsecode to be CREATED",HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());

        // When: User2 sends a message
        MessageDTO message2 = new MessageDTO(chat.getId(), savedUser2.getId(), "Hello from IntegrationTestName2");
        ResponseEntity<Message> response2 = messageController.createMessage(message2);

        // Then
        assertEquals("Expected responsecode to be CREATED",HttpStatus.CREATED, response2.getStatusCode());
        assertNotNull(response2.getBody());

        // Fetch messages
        List<Message> messages = messageController.getMessagesByChat(chat.getId());
        assertEquals("expected amount of messages to be 2",2, messages.size());
    }

    @Test
    public void testNotFindingMatchWrongCategory(){
        User user1 = new User();
        user1.setName("IntegrationTestName");
        user1.setEmail("IntegrationTestEmail@student.su.se");
        user1.setPassword("IntegrationTestPassword");
        userController.registerUser(user1);

        User user2 = new User();
        user2.setName("IntegrationTestName2");
        user2.setEmail("IntegrationTestEmail2@student.su.se");
        user2.setPassword("IntegrationTestPassword2");
        userController.registerUser(user2);

        User savedUser1 = userRepository.findByName("IntegrationTestName");
        User savedUser2 = userRepository.findByName("IntegrationTestName2");

        Category category = categoryRepository.findByName("Go for a walk");
        Category category2 = categoryRepository.findByName("Grab A Fika");

        Map<String, Object> availabilityData = new HashMap<>();
        availabilityData.put("available", true);
        availabilityData.put("totalMinutes", 30);
        availabilityData.put("activityId", category.getId().intValue());


        Map<String, Object> availabilityData2 = new HashMap<>();
        availabilityData2.put("available", true);
        availabilityData2.put("totalMinutes", 30);
        availabilityData2.put("activityId", category2.getId().intValue());

        userController.updateAvailability(savedUser1.getId(), availabilityData);
        userController.updateAvailability(savedUser2.getId(), availabilityData2);

        boolean isMatch = userController.matchUser(savedUser1);
        assertFalse(isMatch, "User should not be matching");
    }

    @Test
    public void testMatchSpontaneousWithOther(){
        User user1 = new User();
        user1.setName("IntegrationTestName");
        user1.setEmail("IntegrationTestEmail@student.su.se");
        user1.setPassword("IntegrationTestPassword");
        userController.registerUser(user1);

        User user2 = new User();
        user2.setName("IntegrationTestName2");
        user2.setEmail("IntegrationTestEmail2@student.su.se");
        user2.setPassword("IntegrationTestPassword2");
        userController.registerUser(user2);

        User savedUser1 = userRepository.findByName("IntegrationTestName");
        User savedUser2 = userRepository.findByName("IntegrationTestName2");

        Category category = categoryRepository.findByName("Go for a walk");
        Category category2 = categoryRepository.findByName("Spontaneous fun");

        Map<String, Object> availabilityData = new HashMap<>();
        availabilityData.put("available", true);
        availabilityData.put("totalMinutes", 30);
        availabilityData.put("activityId", category.getId().intValue());


        Map<String, Object> availabilityData2 = new HashMap<>();
        availabilityData2.put("available", true);
        availabilityData2.put("totalMinutes", 30);
        availabilityData2.put("activityId", category2.getId().intValue());

        userController.updateAvailability(savedUser1.getId(), availabilityData);
        userController.updateAvailability(savedUser2.getId(), availabilityData2);

        boolean isMatch = userController.matchUser(savedUser1);
        assertTrue(isMatch, "Users should be matching, and are not");
    }

}





