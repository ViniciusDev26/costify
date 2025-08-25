package br.unifor.costify.domain.valueobject;

import org.junit.jupiter.api.Test;

class RecipeIngredientTest {
  @Test
  void createRecipeIngredient_withValidValues_shouldInitializeCorrectly() {
    Id ingredientId = Id.of("ingredient-123");
    RecipeIngredient recipeIngredient = new RecipeIngredient(ingredientId, 2.5, Unit.KG);

    assert recipeIngredient.getIngredientId().equals(ingredientId);
    assert recipeIngredient.getQuantity() == 2.5;
    assert recipeIngredient.getUnit() == Unit.KG;
  }

  @Test
  void createRecipeIngredient_withNullIngredientId_shouldThrowException() {
    try {
      new RecipeIngredient(null, 2.5, Unit.KG);
      assert false;
    } catch (IllegalArgumentException ignored) {
    }
  }

  @Test
  void createRecipeIngredient_withZeroQuantity_shouldThrowException() {
    Id ingredientId = Id.of("ingredient-123");
    try {
      new RecipeIngredient(ingredientId, 0, Unit.KG);
      assert false;
    } catch (IllegalArgumentException ignored) {
    }
  }

  @Test
  void createRecipeIngredient_withNegativeQuantity_shouldThrowException() {
    Id ingredientId = Id.of("ingredient-123");
    try {
      new RecipeIngredient(ingredientId, -1.0, Unit.KG);
      assert false;
    } catch (IllegalArgumentException ignored) {
    }
  }

  @Test
  void createRecipeIngredient_withNullUnit_shouldThrowException() {
    Id ingredientId = Id.of("ingredient-123");
    try {
      new RecipeIngredient(ingredientId, 2.5, null);
      assert false;
    } catch (IllegalArgumentException ignored) {
    }
  }

  @Test
  void recipeIngredient_equals_shouldWorkCorrectly() {
    Id ingredientId = Id.of("ingredient-123");
    RecipeIngredient ri1 = new RecipeIngredient(ingredientId, 2.5, Unit.KG);
    RecipeIngredient ri2 = new RecipeIngredient(ingredientId, 2.5, Unit.KG);
    RecipeIngredient ri3 = new RecipeIngredient(ingredientId, 3.0, Unit.KG);

    assert ri1.equals(ri2);
    assert !ri1.equals(ri3);
    assert ri1.hashCode() == ri2.hashCode();
  }
}
