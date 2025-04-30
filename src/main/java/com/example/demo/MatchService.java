package com.example.demo;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
public class MatchService {

    private final MatchRepository matchRepository;
    private final ChatRepository chatRepository;

    public MatchService(MatchRepository matchRepository, ChatRepository chatRepository) {
        this.matchRepository = matchRepository;
        this.chatRepository = chatRepository;
    }

    @Transactional
    public Match createMatch(User user1, User user2) {
        Match match = new Match();
        match.setUser1(user1);
        match.setUser2(user2);

        Chat chat = new Chat();
        chat.setMatch(match);

        match.setChat(chat); // bi-directional linking

        return matchRepository.save(match); // cascades chat due to match's `@OneToOne(mappedBy = "match", cascade = ...)`
    }

}
