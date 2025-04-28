package com.example.demo;

import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryRepository extends JpaRepository<Category, Long> {
    Category findById(long id);
    Category findByName(String name); // to find categories easily
}

