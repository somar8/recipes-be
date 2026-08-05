package com.example.recipes.service;

import com.example.recipes.model.Recipe;
import com.example.recipes.repository.RecipeRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RecipeServiceTest {

    @Mock
    private RecipeRepository repository;

    @InjectMocks
    private RecipeService service;

    private Recipe recipe;

    @BeforeEach
    void setUp() {
        recipe = new Recipe();
        recipe.setId(1L);
        recipe.setTitle("Pasta");
        recipe.setDescription("Classic pasta");
        recipe.setIngredients("pasta, water, salt");
        recipe.setInstructions("Boil water, cook pasta");
        recipe.setPrepTimeMinutes(5);
        recipe.setCookTimeMinutes(10);
        recipe.setServings(2);
    }

    @Test
    void findAll_noFilter_returnsAll() {
        when(repository.findAll()).thenReturn(List.of(recipe));

        List<Recipe> result = service.findAll(null);

        assertThat(result).hasSize(1).contains(recipe);
        verify(repository).findAll();
        verifyNoMoreInteractions(repository);
    }

    @Test
    void findAll_withTitle_searchesByTitle() {
        when(repository.findByTitleContainingIgnoreCase("pasta")).thenReturn(List.of(recipe));

        List<Recipe> result = service.findAll("pasta");

        assertThat(result).hasSize(1).contains(recipe);
        verify(repository).findByTitleContainingIgnoreCase("pasta");
    }

    @Test
    void findAll_blankTitle_returnsAll() {
        when(repository.findAll()).thenReturn(List.of(recipe));

        List<Recipe> result = service.findAll("  ");

        assertThat(result).hasSize(1);
        verify(repository).findAll();
    }

    @Test
    void findById_exists_returnsRecipe() {
        when(repository.findById(1L)).thenReturn(Optional.of(recipe));

        Recipe result = service.findById(1L);

        assertThat(result).isEqualTo(recipe);
    }

    @Test
    void findById_notFound_throwsEntityNotFoundException() {
        when(repository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.findById(99L))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("99");
    }

    @Test
    void create_savesAndReturnsRecipe() {
        when(repository.save(recipe)).thenReturn(recipe);

        Recipe result = service.create(recipe);

        assertThat(result).isEqualTo(recipe);
        verify(repository).save(recipe);
    }

    @Test
    void update_exists_updatesAllFields() {
        Recipe updated = new Recipe();
        updated.setTitle("Updated Pasta");
        updated.setDescription("New desc");
        updated.setIngredients("new ingredients");
        updated.setInstructions("new instructions");
        updated.setPrepTimeMinutes(15);
        updated.setCookTimeMinutes(20);
        updated.setServings(4);

        when(repository.findById(1L)).thenReturn(Optional.of(recipe));
        when(repository.save(any(Recipe.class))).thenAnswer(inv -> inv.getArgument(0));

        Recipe result = service.update(1L, updated);

        assertThat(result.getTitle()).isEqualTo("Updated Pasta");
        assertThat(result.getDescription()).isEqualTo("New desc");
        assertThat(result.getPrepTimeMinutes()).isEqualTo(15);
        assertThat(result.getServings()).isEqualTo(4);
    }

    @Test
    void update_notFound_throwsEntityNotFoundException() {
        when(repository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.update(99L, recipe))
                .isInstanceOf(EntityNotFoundException.class);
    }

    @Test
    void delete_exists_deletesRecipe() {
        when(repository.existsById(1L)).thenReturn(true);

        service.delete(1L);

        verify(repository).deleteById(1L);
    }

    @Test
    void delete_notFound_throwsEntityNotFoundException() {
        when(repository.existsById(99L)).thenReturn(false);

        assertThatThrownBy(() -> service.delete(99L))
                .isInstanceOf(EntityNotFoundException.class);
        verify(repository, never()).deleteById(any());
    }
}
