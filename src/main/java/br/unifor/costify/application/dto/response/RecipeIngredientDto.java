package br.unifor.costify.application.dto.response;

import br.unifor.costify.domain.valueobject.RecipeIngredient;
import br.unifor.costify.domain.valueobject.Unit;

/**
 * DTO for recipe ingredient information in API responses.
 * Converts domain RecipeIngredient to a JSON-friendly format with ingredientId as a string.
 */
public record RecipeIngredientDto(
        String ingredientId,
        double quantity,
        Unit unit
) {
    public static RecipeIngredientDto from(RecipeIngredient recipeIngredient) {
        return new RecipeIngredientDto(
                recipeIngredient.getIngredientId().getValue(),
                recipeIngredient.getQuantity(),
                recipeIngredient.getUnit()
        );
    }
}
