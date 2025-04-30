package com.example.demo;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;


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
}

