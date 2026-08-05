package com.example.recipes.service;

import com.example.recipes.model.GroupRecipes;
import com.example.recipes.model.Recipe;
import com.example.recipes.model.User;
import com.example.recipes.repository.GroupRecipesRepository;
import com.example.recipes.repository.RecipeRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GroupRecipesService {

    private final GroupRecipesRepository groupRecipesRepository;
    private final RecipeRepository recipeRepository;
    private final UserService userService;

    public List<GroupRecipes> findAll() {
        return groupRecipesRepository.findAll();
    }

    public List<GroupRecipes> findByUser(Long userId) {
        return groupRecipesRepository.findByUserId(userId);
    }

    public GroupRecipes findById(Long id) {
        return groupRecipesRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("GroupRecipes not found with id: " + id));
    }

    public GroupRecipes create(GroupRecipes group, String username) {
        User user = userService.findByUsername(username);
        group.setUser(user);
        return groupRecipesRepository.save(group);
    }

    public GroupRecipes update(Long id, GroupRecipes updated) {
        GroupRecipes existing = findById(id);
        existing.setName(updated.getName());
        existing.setDescription(updated.getDescription());
        return groupRecipesRepository.save(existing);
    }

    public void delete(Long id) {
        if (!groupRecipesRepository.existsById(id)) {
            throw new EntityNotFoundException("GroupRecipes not found with id: " + id);
        }
        groupRecipesRepository.deleteById(id);
    }

    public GroupRecipes addRecipe(Long groupId, Long recipeId) {
        GroupRecipes group = findById(groupId);
        Recipe recipe = recipeRepository.findById(recipeId)
                .orElseThrow(() -> new EntityNotFoundException("Recipe not found with id: " + recipeId));
        group.getRecipes().add(recipe);
        return groupRecipesRepository.save(group);
    }

    public GroupRecipes removeRecipe(Long groupId, Long recipeId) {
        GroupRecipes group = findById(groupId);
        group.getRecipes().removeIf(r -> r.getId().equals(recipeId));
        return groupRecipesRepository.save(group);
    }
}