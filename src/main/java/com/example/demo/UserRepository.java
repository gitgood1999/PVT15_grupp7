package com.example.demo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface UserRepository extends JpaRepository<User, Long> {
   User findByEmail(String email);

   User findByCategory(Category category);

   User findById(long id);

   List<User> findByAvailableTrue();

   @Transactional
   @Modifying
   @Query("UPDATE User u SET u.available = CASE WHEN u.available = true THEN false ELSE true END WHERE u.id = :id")
   void toggleAvailableById(@Param("id") long id);


   List<User> findByAvailableTrueAndCategory(Category category);
}
