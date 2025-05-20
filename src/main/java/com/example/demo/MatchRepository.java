package com.example.demo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface MatchRepository extends JpaRepository<UserMatch, Long> {

    // Hämta en match med ID
    UserMatch findById(long id);

    // Kontrollera om match redan finns i båda riktningar
    boolean existsByUser1AndUser2(User user1, User user2);
    boolean existsByUser2AndUser1(User user1, User user2);

    // Alternativt (för mer säkerhet) – en enda query som hanterar båda riktningar:
    @Query("SELECT CASE WHEN COUNT(m) > 0 THEN true ELSE false END " +
            "FROM UserMatch m " +
            "WHERE (m.user1 = :user1 AND m.user2 = :user2) " +
            "   OR (m.user1 = :user2 AND m.user2 = :user1)")
    boolean existsBetweenUsers(User user1, User user2);
}
