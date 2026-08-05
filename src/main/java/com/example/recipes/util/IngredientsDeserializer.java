package com.example.recipes.util;

import com.example.recipes.model.RecipeIngredient;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class IngredientsDeserializer extends StdDeserializer<List<RecipeIngredient>> {

    public IngredientsDeserializer() {
        super(List.class);
    }

    @Override
    public List<RecipeIngredient> deserialize(JsonParser p, DeserializationContext ctx) throws IOException {
        // Legacy format: plain string "flour, eggs, milk"
        if (p.currentToken() == JsonToken.VALUE_STRING) {
            List<RecipeIngredient> list = new ArrayList<>();
            for (String part : p.getText().split(",")) {
                String trimmed = part.trim();
                if (!trimmed.isBlank()) {
                    RecipeIngredient ri = new RecipeIngredient();
                    ri.setName(trimmed);
                    list.add(ri);
                }
            }
            return list;
        }
        // New format: array of objects [{name, quantity, unit}]
        List<RecipeIngredient> list = new ArrayList<>();
        if (p.currentToken() == JsonToken.START_ARRAY) {
            while (p.nextToken() != JsonToken.END_ARRAY) {
                list.add(ctx.readValue(p, RecipeIngredient.class));
            }
        }
        return list;
    }
}