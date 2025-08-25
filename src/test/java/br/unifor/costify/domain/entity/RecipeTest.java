package br.unifor.costify.domain.entity;

import br.unifor.costify.domain.contracts.IdGenerator;
import br.unifor.costify.domain.valueobject.Id;
import br.unifor.costify.domain.valueobject.RecipeIngredient;
import br.unifor.costify.domain.valueobject.Unit;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class RecipeTest {
  private IdGenerator fakeIdGenerator;
  private RecipeIngredient recipeIngredient1;
  private RecipeIngredient recipeIngredient2;

  @BeforeEach
  void setup() {
    fakeIdGenerator = () -> "test-recipe-id-123";

    Id ingredientId1 = Id.of("ingredient-id-1");
    Id ingredientId2 = Id.of("ingredient-id-2");

    recipeIngredient1 = new RecipeIngredient(ingredientId1, 0.5, Unit.KG);
    recipeIngredient2 = new RecipeIngredient(ingredientId2, 0.2, Unit.KG);
  }

  @Test
  void createRecipe_withIdGenerator_shouldInitializeCorrectly() {
    Recipe recipe =
        new Recipe(fakeIdGenerator, "Bread Recipe", List.of(recipeIngredient1, recipeIngredient2));

    assert recipe.getId().getValue().equals("test-recipe-id-123");
    assert recipe.getName().equals("Bread Recipe");
    assert recipe.getIngredients().size() == 2;
    assert recipe.getIngredients().contains(recipeIngredient1);
    assert recipe.getIngredients().contains(recipeIngredient2);
  }

  @Test
  void createRecipe_withId_shouldInitializeCorrectly() {
    Id existingId = Id.of("existing-recipe-id");
    Recipe recipe = new Recipe(existingId, "Cake Recipe", List.of(recipeIngredient1));

    assert recipe.getId().getValue().equals("existing-recipe-id");
    assert recipe.getName().equals("Cake Recipe");
    assert recipe.getIngredients().size() == 1;
    assert recipe.getIngredients().contains(recipeIngredient1);
  }

  @Test
  void createRecipe_withEmptyIngredients_shouldThrowException() {
    try {
      new Recipe(fakeIdGenerator, "Empty Recipe", List.of());
      assert false;
    } catch (IllegalArgumentException ignored) {
    }
  }

  @Test
  void createRecipe_withNullIngredients_shouldThrowException() {
    try {
      new Recipe(fakeIdGenerator, "Null Ingredients Recipe", null);
      assert false;
    } catch (IllegalArgumentException ignored) {
    }
  }

  @Test
  void createRecipe_withInvalidName_shouldThrowException() {
    try {
      new Recipe(fakeIdGenerator, null, List.of(recipeIngredient1));
      assert false;
    } catch (IllegalArgumentException ignored) {
    }

    try {
      new Recipe(fakeIdGenerator, "", List.of(recipeIngredient1));
      assert false;
    } catch (IllegalArgumentException ignored) {
    }

    try {
      new Recipe(fakeIdGenerator, "   ", List.of(recipeIngredient1));
      assert false;
    } catch (IllegalArgumentException ignored) {
    }
  }

  @Test
  void updateRecipe_shouldChangeFieldsCorrectly() {
    Recipe recipe = new Recipe(fakeIdGenerator, "Original Recipe", List.of(recipeIngredient1));

    recipe.setName("Updated Recipe");
    recipe.setIngredients(List.of(recipeIngredient1, recipeIngredient2));

    assert recipe.getName().equals("Updated Recipe");
    assert recipe.getIngredients().size() == 2;
    assert recipe.getIngredients().contains(recipeIngredient1);
    assert recipe.getIngredients().contains(recipeIngredient2);
    assert recipe.getId().getValue().equals("test-recipe-id-123");
  }

  @Test
  void updateRecipe_withEmptyIngredients_shouldThrowException() {
    Recipe recipe = new Recipe(fakeIdGenerator, "Test Recipe", List.of(recipeIngredient1));

    try {
      recipe.setIngredients(List.of());
      assert false;
    } catch (IllegalArgumentException ignored) {
    }
  }

  @Test
  void updateRecipe_withInvalidName_shouldThrowException() {
    Recipe recipe = new Recipe(fakeIdGenerator, "Test Recipe", List.of(recipeIngredient1));

    try {
      recipe.setName("");
      assert false;
    } catch (IllegalArgumentException ignored) {
    }

    try {
      recipe.setName(null);
      assert false;
    } catch (IllegalArgumentException ignored) {
    }
  }

  @Test
  void addIngredient_shouldAddToList() {
    Recipe recipe = new Recipe(fakeIdGenerator, "Test Recipe", List.of(recipeIngredient1));

    recipe.addIngredient(recipeIngredient2);

    assert recipe.getIngredients().size() == 2;
    assert recipe.getIngredients().contains(recipeIngredient1);
    assert recipe.getIngredients().contains(recipeIngredient2);
  }

  @Test
  void removeIngredient_shouldRemoveFromList() {
    Recipe recipe =
        new Recipe(fakeIdGenerator, "Test Recipe", List.of(recipeIngredient1, recipeIngredient2));

    recipe.removeIngredient(recipeIngredient1.getIngredientId());

    assert recipe.getIngredients().size() == 1;
    assert recipe.getIngredients().contains(recipeIngredient2);
    assert !recipe.getIngredients().contains(recipeIngredient1);
  }

  @Test
  void removeIngredient_leavingEmpty_shouldThrowException() {
    Recipe recipe = new Recipe(fakeIdGenerator, "Test Recipe", List.of(recipeIngredient1));

    try {
      recipe.removeIngredient(recipeIngredient1.getIngredientId());
      assert false;
    } catch (IllegalArgumentException ignored) {
    }
  }
}
