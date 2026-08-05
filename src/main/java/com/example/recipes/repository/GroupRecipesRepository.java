package com.example.recipes.repository;

import com.example.recipes.model.GroupRecipes;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface GroupRecipesRepository extends JpaRepository<GroupRecipes, Long> {
    List<GroupRecipes> findByUserId(Long userId);
}