package com.example.demo;

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

    public MatchService(
            MatchRepository matchRepository,
            ChatRepository chatRepository,
            UserRepository userRepository,
            AvailabilityService availabilityService
    ) {
        this.matchRepository = matchRepository;
        this.chatRepository = chatRepository;
        this.userRepository = userRepository;
        this.availabilityService = availabilityService;
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

        Chat chat = new Chat();
        chat.setMatch(match);
        match.setChat(chat);

        addToPreviousMatches(user1, user2);
        addToPreviousMatches(user2, user1);

        userRepository.save(user1);
        userRepository.save(user2);

        // Toggle availability for both users
        availabilityService.toggleAvailability(user1.getId());
        availabilityService.toggleAvailability(user2.getId());

        return matchRepository.save(match);



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
        // Return Optional to avoid NullPointer and align with repository
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
        // Läs hem dig själv med historik
        User self = userRepository.findWithPreviousMatchesById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        Available myStatus = self.getAvailableStatus();
        if (myStatus == null || !myStatus.isAvailable()) {
            // Är du inte markerad som available → ingen match
            return Optional.empty();
        }

        LocalDateTime myFrom = myStatus.getAvailableSince();
        LocalDateTime myTo   = myStatus.getAvailableUntil();

        return userRepository.findAll().stream()
                // 1) Ej dig själv
                .filter(u -> !(u.getId() ==(userId)))

                // 2) Partnern måste vara tillgänglig
                .filter(u -> {
                    Available s = u.getAvailableStatus();
                    return s != null && Boolean.TRUE.equals(s.isAvailable());
                })

                // 3) Tidsfönstren måste överlappa:
                .filter(u -> {
                    Available s        = u.getAvailableStatus();
                    LocalDateTime pFrom = s.getAvailableSince();
                    LocalDateTime pTo   = s.getAvailableUntil();

                    if (myFrom == null || pFrom == null) {
                        return false;
                    }

                    // Tolka "until == null" som oändligt framåt
                    LocalDateTime endSelf    = (myTo != null) ? myTo    : LocalDateTime.MAX;
                    LocalDateTime endPartner = (pTo  != null) ? pTo     : LocalDateTime.MAX;

                    // Beräkna senaste starttid och tidigaste sluttid
                    LocalDateTime latestStart = myFrom.isAfter(pFrom) ? myFrom : pFrom;
                    LocalDateTime earliestEnd = endSelf.isBefore(endPartner) ? endSelf : endPartner;

                    boolean overlap = !latestStart.isAfter(earliestEnd);

                    // DEBUG-utskrift för att se värden i loggen
                    System.out.printf(
                            "DEBUG overlap? self=%s–%s partner=%s–%s → %b%n",
                            myFrom, myTo, pFrom, pTo, overlap
                    );

                    return overlap;
                })

                // 4) Samma kategori
                .filter(u -> {
                    Long selfCat = self.getCategory().getId();
                    Long partnerCat = u.getCategory().getId();
                    return selfCat.equals(partnerCat)
                            || selfCat == 3  // Spontaneous Fun från self
                            || partnerCat == 3;  // Spontaneous Fun från partner
                })

                // 5) Ej tidigare matchad
                .filter(u -> !self.getPreviousMatches().contains(u))

                .findAny();
    }




    public Optional<UserMatch> findMatchForUser(Long userId) {
        return matchRepository.findAll().stream()
                .filter(m -> m.getUser1().getId()==(userId) || m.getUser2().getId()==(userId))
                .findFirst();
    }


}
