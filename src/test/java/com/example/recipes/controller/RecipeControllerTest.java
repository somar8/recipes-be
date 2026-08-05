package com.example.recipes.controller;

import com.example.recipes.model.Recipe;
import com.example.recipes.service.RecipeService;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(RecipeController.class)
class RecipeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
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
    void getAll_returnsListOfRecipes() throws Exception {
        when(service.findAll(null)).thenReturn(List.of(recipe));

        mockMvc.perform(get("/api/recipes"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].title").value("Pasta"));
    }

    @Test
    void getAll_withTitleFilter_passesFilterToService() throws Exception {
        when(service.findAll("pasta")).thenReturn(List.of(recipe));

        mockMvc.perform(get("/api/recipes").param("title", "pasta"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].title").value("Pasta"));

        verify(service).findAll("pasta");
    }

    @Test
    void getById_found_returns200() throws Exception {
        when(service.findById(1L)).thenReturn(recipe);

        mockMvc.perform(get("/api/recipes/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.title").value("Pasta"));
    }

    @Test
    void getById_notFound_returns404() throws Exception {
        when(service.findById(99L)).thenThrow(new EntityNotFoundException("Recipe not found with id: 99"));

        mockMvc.perform(get("/api/recipes/99"))
                .andExpect(status().isNotFound());
    }

    @Test
    void create_validRecipe_returns201() throws Exception {
        when(service.create(any(Recipe.class))).thenReturn(recipe);

        mockMvc.perform(post("/api/recipes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(recipe)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.title").value("Pasta"));
    }

    @Test
    void create_missingTitle_returns400() throws Exception {
        Recipe invalid = new Recipe();
        invalid.setDescription("No title");

        mockMvc.perform(post("/api/recipes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalid)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void update_found_returns200() throws Exception {
        when(service.update(eq(1L), any(Recipe.class))).thenReturn(recipe);

        mockMvc.perform(put("/api/recipes/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(recipe)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Pasta"));
    }

    @Test
    void update_notFound_returns404() throws Exception {
        when(service.update(eq(99L), any(Recipe.class)))
                .thenThrow(new EntityNotFoundException("Recipe not found with id: 99"));

        mockMvc.perform(put("/api/recipes/99")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(recipe)))
                .andExpect(status().isNotFound());
    }

    @Test
    void delete_found_returns204() throws Exception {
        doNothing().when(service).delete(1L);

        mockMvc.perform(delete("/api/recipes/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    void delete_notFound_returns404() throws Exception {
        doThrow(new EntityNotFoundException("Recipe not found with id: 99"))
                .when(service).delete(99L);

        mockMvc.perform(delete("/api/recipes/99"))
                .andExpect(status().isNotFound());
    }
}