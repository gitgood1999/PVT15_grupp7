package com.example.demo;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
   User findByEmail(String email);

   User findByName(String name);

   User findById(long id);

   void deleteByEmail(String email);

   @Query("""
    SELECT u FROM User u
    JOIN u.availableStatus a
    WHERE (u.category.name = :categoryName OR u.category.name = 'Spontaneous fun')
    AND a.available = true
    AND u.id != :userId
    AND u NOT IN :previousMatches
""")
   List<User> findByCategoryOrSpontaneousAndAvailableTrueExcludingUser(
           @Param("categoryName") String categoryName,
           @Param("userId") Long userId,
           @Param("previousMatches") List<User> previousMatches
   );




   @Transactional
   @Modifying
   @Query("UPDATE User u SET u.category = :category WHERE u.id = :userId")
   void setUserCategory(@Param("userId") Long userId, @Param("category") Category category);

   @Query("""
    SELECT u FROM User u
    JOIN Available a ON a.user = u
    WHERE u.id != :userId AND a.available = true
""")




   List<User> findAllExcludingUser(@Param("userId") Long userId);

   @Query("SELECT u FROM User u LEFT JOIN FETCH u.previousMatches WHERE u.id = :id")
   Optional<User> findWithPreviousMatchesById(@Param("id") Long id);



   @Query("""
    SELECT u FROM User u
    JOIN Available a ON a.user = u
    WHERE (u.category.name = :categoryName OR u.category.name = 'Spontaneous fun')
    AND a.available = true
    AND u.id != :userId
""")
   List<User> findByCategoryOrSpontaneousAndAvailableTrueExcludingUserNoHistory(
           @Param("categoryName") String categoryName,
           @Param("userId") Long userId
   );





   @Query("SELECT u FROM User u " +
           "WHERE u.availableStatus.available = true " +
           "AND u.category.name = :categoryName " +
           "AND u.id <> :userId " +
           "AND (u NOT IN :excludedUsers) " +
           "AND u.availableStatus.availableSince <= :endTime " +
           "AND (u.availableStatus.availableUntil IS NULL OR u.availableStatus.availableUntil >= :startTime)")
   List<User> findAvailableUsersWithMatchingCategoryAndTimeOverlap(
           @Param("categoryName") String categoryName,
           @Param("userId") Long userId,
           @Param("excludedUsers") List<User> excludedUsers,
           @Param("startTime") LocalDateTime startTime,
           @Param("endTime") LocalDateTime endTime
   );



}
