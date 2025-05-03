package com.example.demo;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


@Service
public class MatchService {

    private final MatchRepository matchRepository;
    private final ChatRepository chatRepository;
    private final UserRepository userRepository;

    public MatchService(MatchRepository matchRepository, ChatRepository chatRepository, UserRepository userRepository) {
        this.matchRepository = matchRepository;
        this.chatRepository = chatRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public UserMatch createMatch(User user1, User user2) {
        // Load users with previousMatches initialized
        user1 = userRepository.findWithPreviousMatchesById(user1.getId())
                .orElseThrow(() -> new IllegalArgumentException("User1 not found"));
        user2 = userRepository.findWithPreviousMatchesById(user2.getId())
                .orElseThrow(() -> new IllegalArgumentException("User2 not found"));

        // Check if users have already matched
        if (user1.getPreviousMatches().contains(user2) || user2.getPreviousMatches().contains(user1)) {
            System.out.println("Users already matched");
            return null;
        }

        // Create the match and chat
        UserMatch match = new UserMatch();
        match.setUser1(user1);
        match.setUser2(user2);

        Chat chat = new Chat();
        chat.setMatch(match);
        match.setChat(chat);

        // Update previous matches
        addToPreviousMatches(user1, user2);
        addToPreviousMatches(user2, user1);

        userRepository.save(user1);
        userRepository.save(user2);

        return matchRepository.save(match);
    }


    private void addToPreviousMatches(User source, User target) {
        List<User> prev = source.getPreviousMatches();
        if (!prev.contains(target)) {
            if (prev.size() >= 5) {
                prev.removeFirst();
            }
            prev.add(target);
        }
    }

    public UserMatch getMatch(long id) {
        if (matchRepository.existsById(id)) {
            return matchRepository.findById(id);
        }
        return null;
    }
}

