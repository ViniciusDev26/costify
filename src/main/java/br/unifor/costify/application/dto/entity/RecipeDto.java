package br.unifor.costify.application.dto.entity;

import br.unifor.costify.domain.entity.Recipe;
import br.unifor.costify.domain.valueobject.RecipeIngredient;

import java.util.List;

public record RecipeDto(
    String id,
    String name,
    List<RecipeIngredient> ingredients
) {
    
    public static RecipeDto from(Recipe recipe) {
        return new RecipeDto(
            recipe.getId().getValue(),
            recipe.getName(),
            recipe.getIngredients()
        );
    }
}