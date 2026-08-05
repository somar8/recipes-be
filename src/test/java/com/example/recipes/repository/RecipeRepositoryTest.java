package com.example.recipes.repository;

import com.example.recipes.model.Recipe;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class RecipeRepositoryTest {

    @Autowired
    private RecipeRepository repository;

    private Recipe recipe;

    @BeforeEach
    void setUp() {
        recipe = new Recipe();
        recipe.setTitle("Spaghetti Carbonara");
        recipe.setDescription("Classic Italian pasta");
        recipe.setIngredients("spaghetti, eggs, pancetta, parmesan");
        recipe.setInstructions("Cook pasta, mix with eggs and pancetta");
        recipe.setPrepTimeMinutes(10);
        recipe.setCookTimeMinutes(20);
        recipe.setServings(2);
        repository.save(recipe);
    }

    @Test
    void save_persistsRecipe() {
        assertThat(recipe.getId()).isNotNull();
    }

    @Test
    void findById_returnsRecipe() {
        Optional<Recipe> result = repository.findById(recipe.getId());

        assertThat(result).isPresent();
        assertThat(result.get().getTitle()).isEqualTo("Spaghetti Carbonara");
    }

    @Test
    void findAll_returnsAllRecipes() {
        Recipe another = new Recipe();
        another.setTitle("Pizza Margherita");
        repository.save(another);

        List<Recipe> result = repository.findAll();

        assertThat(result).hasSize(2);
    }

    @Test
    void findByTitleContainingIgnoreCase_matchesPartialTitle() {
        List<Recipe> result = repository.findByTitleContainingIgnoreCase("carbonara");

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getTitle()).isEqualTo("Spaghetti Carbonara");
    }

    @Test
    void findByTitleContainingIgnoreCase_caseInsensitive() {
        List<Recipe> result = repository.findByTitleContainingIgnoreCase("SPAGHETTI");

        assertThat(result).hasSize(1);
    }

    @Test
    void findByTitleContainingIgnoreCase_noMatch_returnsEmpty() {
        List<Recipe> result = repository.findByTitleContainingIgnoreCase("tacos");

        assertThat(result).isEmpty();
    }

    @Test
    void delete_removesRecipe() {
        repository.deleteById(recipe.getId());

        assertThat(repository.findById(recipe.getId())).isEmpty();
    }

    @Test
    void update_changesFields() {
        recipe.setTitle("Updated Carbonara");
        recipe.setServings(4);
        repository.save(recipe);

        Recipe updated = repository.findById(recipe.getId()).orElseThrow();
        assertThat(updated.getTitle()).isEqualTo("Updated Carbonara");
        assertThat(updated.getServings()).isEqualTo(4);
    }
}