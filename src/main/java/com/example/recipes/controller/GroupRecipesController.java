package com.example.recipes.controller;

import com.example.recipes.model.GroupRecipes;
import com.example.recipes.service.GroupRecipesService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/group-recipes")
@RequiredArgsConstructor
public class GroupRecipesController {

    private final GroupRecipesService service;

    @GetMapping
    public List<GroupRecipes> getAll() {
        return service.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<GroupRecipes> getById(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(service.findById(id));
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping
    public ResponseEntity<GroupRecipes> create(@Valid @RequestBody GroupRecipes group,
                                               @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.create(group, userDetails.getUsername()));
    }

    @PutMapping("/{id}")
    public ResponseEntity<GroupRecipes> update(@PathVariable Long id,
                                               @Valid @RequestBody GroupRecipes group) {
        try {
            return ResponseEntity.ok(service.update(id, group));
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        try {
            service.delete(id);
            return ResponseEntity.noContent().build();
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/{groupId}/recipes/{recipeId}")
    public ResponseEntity<GroupRecipes> addRecipe(@PathVariable Long groupId, @PathVariable Long recipeId) {
        try {
            return ResponseEntity.ok(service.addRecipe(groupId, recipeId));
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{groupId}/recipes/{recipeId}")
    public ResponseEntity<GroupRecipes> removeRecipe(@PathVariable Long groupId, @PathVariable Long recipeId) {
        try {
            return ResponseEntity.ok(service.removeRecipe(groupId, recipeId));
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }
}