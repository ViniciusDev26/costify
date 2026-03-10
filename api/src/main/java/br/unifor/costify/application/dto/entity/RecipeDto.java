package br.unifor.costify.application.dto.entity;

import br.unifor.costify.application.dto.response.RecipeIngredientDto;
import br.unifor.costify.domain.entity.Recipe;
import java.math.BigDecimal;
import java.util.List;

public record RecipeDto(String id, String name, List<RecipeIngredientDto> ingredients, BigDecimal totalCost) {

  public static RecipeDto from(Recipe recipe) {
    List<RecipeIngredientDto> ingredientDtos = recipe.getIngredients().stream()
            .map(RecipeIngredientDto::from)
            .toList();

    return new RecipeDto(
            recipe.getId().getValue(),
            recipe.getName(),
            ingredientDtos,
            recipe.getTotalCost().getAmount()
    );
  }
}
