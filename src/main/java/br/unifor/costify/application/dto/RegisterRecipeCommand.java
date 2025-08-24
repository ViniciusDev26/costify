package br.unifor.costify.application.dto;

import br.unifor.costify.domain.valueobject.RecipeIngredient;

import java.util.List;

public record RegisterRecipeCommand(
    String name,
    List<RecipeIngredient> ingredients
) {
    
    public RegisterRecipeCommand {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Recipe name cannot be null or empty");
        }
        if (ingredients == null || ingredients.isEmpty()) {
            throw new IllegalArgumentException("Recipe must have at least one ingredient");
        }
    }
}