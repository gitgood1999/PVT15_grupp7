package com.example.demo;

import com.google.firebase.messaging.FirebaseMessagingException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class MatchService {

    private final MatchRepository matchRepository;
    private final ChatRepository chatRepository;
    private final UserRepository userRepository;
    private final AvailabilityService availabilityService;
    private final FCMService fcmService;

    public MatchService(
            MatchRepository matchRepository,
            ChatRepository chatRepository,
            UserRepository userRepository,
            AvailabilityService availabilityService,
            FCMService fcmService
    ) {
        this.matchRepository = matchRepository;
        this.chatRepository = chatRepository;
        this.userRepository = userRepository;
        this.availabilityService = availabilityService;
        this.fcmService = fcmService;
    }

    @Transactional
    public UserMatch createMatch(User user1, User user2) {
        user1 = userRepository.findWithPreviousMatchesById(user1.getId())
                .orElseThrow(() -> new IllegalArgumentException("User1 not found"));
        user2 = userRepository.findWithPreviousMatchesById(user2.getId())
                .orElseThrow(() -> new IllegalArgumentException("User2 not found"));

        if (user1.getPreviousMatches().contains(user2) || user2.getPreviousMatches().contains(user1)) {
            return null;
        }

        UserMatch match = new UserMatch();
        match.setUser1(user1);
        match.setUser2(user2);
        match = matchRepository.save(match);

        Chat chat = new Chat();
        chat.setMatch(match);
        chat = chatRepository.save(chat);
        match.setChat(chat);

        addToPreviousMatches(user1, user2);
        addToPreviousMatches(user2, user1);
        userRepository.save(user1);
        userRepository.save(user2);

        availabilityService.toggleAvailability(user1.getId());
        availabilityService.toggleAvailability(user2.getId());

        String title = "🎉 Ny match!";
        String body = "Du har blivit matchad – starta en konversation nu!";
        try {
            if (user1.getFcmToken() != null) {
                fcmService.sendMessageWithMatchData(
                        user1.getFcmToken(),
                        title,
                        body,
                        chat.getId(),
                        match.getId(),
                        "match",
                        user2.getName(),
                        user2.getAvatarIndex()
                );
            }
            if (user2.getFcmToken() != null) {
                fcmService.sendMessageWithMatchData(
                        user2.getFcmToken(),
                        title,
                        body,
                        chat.getId(),
                        match.getId(),
                        "match",
                        user1.getName(),
                        user1.getAvatarIndex()
                );
            }
        } catch (FirebaseMessagingException e) {
            System.err.println("⚠️ Kunde inte skicka FCM vid matchning: " + e.getMessage());
        }

        return match;
    }

    private void addToPreviousMatches(User source, User target) {
        List<User> prev = source.getPreviousMatches();
        if (!prev.contains(target)) {
            if (prev.size() >= 5) {
                prev.remove(0);
            }
            prev.add(target);
        }
    }

    public Optional<UserMatch> getMatch(long id) {
        return Optional.ofNullable(matchRepository.findById(id));
    }

    @Transactional
    public boolean deleteMatch(Long matchId) {
        return matchRepository.findById(matchId)
                .map(m -> {
                    matchRepository.delete(m);
                    return true;
                })
                .orElse(false);
    }

    @Transactional
    public Optional<User> findAvailableMatchForUser(Long userId) {
        User self = userRepository.findWithPreviousMatchesById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        Available myStatus = self.getAvailableStatus();
        if (myStatus == null || !myStatus.isAvailable()) {
            return Optional.empty();
        }

        LocalDateTime myFrom = myStatus.getAvailableSince();
        LocalDateTime myTo = myStatus.getAvailableUntil();

        return userRepository.findAll().stream()
                .filter(u -> u.getId() != userId)
                .filter(u -> {
                    Available s = u.getAvailableStatus();
                    return s != null && Boolean.TRUE.equals(s.isAvailable());
                })
                .filter(u -> {
                    Available s = u.getAvailableStatus();
                    LocalDateTime pFrom = s.getAvailableSince();
                    LocalDateTime pTo = s.getAvailableUntil();

                    if (myFrom == null || pFrom == null) return false;

                    LocalDateTime endSelf = (myTo != null) ? myTo : LocalDateTime.MAX;
                    LocalDateTime endPartner = (pTo != null) ? pTo : LocalDateTime.MAX;

                    LocalDateTime latestStart = myFrom.isAfter(pFrom) ? myFrom : pFrom;
                    LocalDateTime earliestEnd = endSelf.isBefore(endPartner) ? endSelf : endPartner;

                    return !latestStart.isAfter(earliestEnd);
                })
                .filter(u -> {
                    Long selfCat = self.getCategory().getId();
                    Long partnerCat = u.getCategory().getId();
                    return selfCat.equals(partnerCat) || selfCat == 3 || partnerCat == 3;
                })
                .filter(u -> !self.getPreviousMatches().contains(u))
                .findAny();
    }

    public Optional<UserMatch> findMatchForUser(Long userId) {
        return matchRepository.findAll().stream()
                .filter(m -> m.getUser1().getId() == userId || m.getUser2().getId() == userId)
                .findFirst();
    }
}
