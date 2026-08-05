package com.example.recipes.service;

import com.example.recipes.model.Recipe;
import com.example.recipes.model.RecipeIngredient;
import com.example.recipes.model.User;
import com.example.recipes.repository.RecipeRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RecipeService {

    private final RecipeRepository repository;
    private final UserService userService;

    public List<Recipe> findAll(String title) {
        if (title != null && !title.isBlank()) {
            return repository.findByTitleContainingIgnoreCase(title);
        }
        return repository.findAll();
    }

    public Recipe findById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Recipe not found with id: " + id));
    }

    @Transactional
    public Recipe create(Recipe recipe, String username) {
        User user = userService.findByUsername(username);
        recipe.setUser(user);
        recipe.getIngredients().forEach(i -> i.setRecipe(recipe));
        return repository.save(recipe);
    }

    @Transactional
    public Recipe update(Long id, Recipe updated) {
        Recipe existing = findById(id);
        existing.setTitle(updated.getTitle());
        existing.setDescription(updated.getDescription());
        existing.setInstructions(updated.getInstructions());
        existing.setPrepTimeMinutes(updated.getPrepTimeMinutes());
        existing.setCookTimeMinutes(updated.getCookTimeMinutes());
        existing.setServings(updated.getServings());
        existing.getIngredients().clear();
        for (RecipeIngredient ingredient : updated.getIngredients()) {
            ingredient.setRecipe(existing);
            existing.getIngredients().add(ingredient);
        }
        return repository.save(existing);
    }

    public void delete(Long id) {
        if (!repository.existsById(id)) {
            throw new EntityNotFoundException("Recipe not found with id: " + id);
        }
        repository.deleteById(id);
    }
}