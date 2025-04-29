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

   @Query("""
    SELECT u FROM User u
    JOIN Available a ON a.user = u
    WHERE (u.category.name = :categoryName OR u.category.name = 'Whatever')
    AND a.available = true
    AND u.id != :userId
""")
   List<User> findByCategoryOrWhateverAndAvailableTrueExcludingUser(@Param("categoryName") String categoryName,
                                                                    @Param("userId") Long userId);


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


}
