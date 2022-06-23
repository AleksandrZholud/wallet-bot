package com.company.telegrambot.repository;

import com.company.telegrambot.model.Category;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryRepository extends JpaRepository<Category, Long> {

    Category getById(Long id);
}
