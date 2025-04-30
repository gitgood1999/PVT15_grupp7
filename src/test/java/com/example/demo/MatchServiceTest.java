package com.example.demo;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;


import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
public class MatchServiceTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserMatchRepository userMatchRepository;

    @Autowired
    private ChatRepository chatRepository;

    @Autowired
    private MatchService matchService;

    @Test
    @Transactional
    public void testCreateMatch_createsUserMatchAndChat() {
        UserMatch match = matchService.getMatch(6L);

        // Assert: UserMatch is persisted
        assertThat(match.getUser1()).isEqualTo(userRepository.findById(50L));
        assertThat(match.getUser2()).isEqualTo(userRepository.findById(51L));

        // Assert: Chat is created and linked
        Chat chat = chatRepository.findByMatch(match);
        assertThat(chat).isNotNull();
        assertThat(chat.getMatch()).isEqualTo(match);
        assertThat(chat.getMessages()).isEmpty();  // initial state
    }

    @Test
    @Transactional
    public void testSendingMessages(){
        UserMatch match = matchService.getMatch(6L);
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

        // Persist messages
        match.getChat().getMessages().add(message1);
        match.getChat().getMessages().add(message2);
        chatRepository.save(match.getChat());  // cascade save should handle messages if properly configured
    }
}

