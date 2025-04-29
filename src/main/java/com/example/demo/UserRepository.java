package com.example.demo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface UserRepository extends JpaRepository<User, Long> {
   User findByEmail(String email);

   User findById(long id);

   @Transactional
   @Modifying
   @Query("SELECT u FROM User u WHERE (u.category.name = :categoryName OR u.category.name = 'Whatever') AND u.available = true AND u.id != :userId")
   List<User> findByCategoryOrWhateverAndAvailableTrueExcludingUser(@Param("userId") Long userId);

   @Transactional
   @Modifying
   @Query("UPDATE User u SET u.category = :category WHERE u.id = :userId")
   void setUserCategory(@Param("userId") Long userId, @Param("category") Category category);

   // Custom query to exclude a specific user based on their ID
   @Query("SELECT u FROM User u WHERE (u.id != :userId) AND u.available = true")
   List<User> findAllExcludingUser(@Param("userId") Long userId);

}
