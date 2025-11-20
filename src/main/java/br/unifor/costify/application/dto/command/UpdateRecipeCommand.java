package br.unifor.costify.application.dto.command;

import br.unifor.costify.domain.valueobject.RecipeIngredient;
import java.util.List;

public record UpdateRecipeCommand(String name, List<RecipeIngredient> ingredients) {

  public UpdateRecipeCommand {
    if (name == null || name.isBlank()) {
      throw new IllegalArgumentException("Recipe name cannot be null or empty");
    }
    if (ingredients == null || ingredients.isEmpty()) {
      throw new IllegalArgumentException("Recipe must have at least one ingredient");
    }
  }
}
