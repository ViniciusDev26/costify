package br.unifor.costify.application.dto;

import br.unifor.costify.domain.contracts.IdGenerator;
import br.unifor.costify.domain.entity.Recipe;
import br.unifor.costify.domain.valueobject.Id;
import br.unifor.costify.domain.valueobject.RecipeIngredient;
import br.unifor.costify.domain.valueobject.Unit;
import org.junit.jupiter.api.Test;

import java.util.List;

class RecipeDtoTest {

    @Test
    void from_withRecipe_shouldCreateCorrectDto() {
        IdGenerator fakeIdGenerator = () -> "test-recipe-id-123";
        Id ingredientId1 = Id.of("ingredient-1");
        Id ingredientId2 = Id.of("ingredient-2");
        RecipeIngredient recipeIngredient1 = new RecipeIngredient(ingredientId1, 0.5, Unit.KG);
        RecipeIngredient recipeIngredient2 = new RecipeIngredient(ingredientId2, 0.2, Unit.KG);
        List<RecipeIngredient> ingredients = List.of(recipeIngredient1, recipeIngredient2);
        
        Recipe recipe = new Recipe(fakeIdGenerator, "Bread Recipe", ingredients);

        RecipeDto dto = RecipeDto.from(recipe);

        assert dto.id().equals("test-recipe-id-123");
        assert dto.name().equals("Bread Recipe");
        assert dto.ingredients().size() == 2;
        assert dto.ingredients().contains(recipeIngredient1);
        assert dto.ingredients().contains(recipeIngredient2);
    }

    @Test
    void from_withDifferentRecipe_shouldCreateCorrectDto() {
        IdGenerator fakeIdGenerator = () -> "cake-recipe-id-456";
        Id ingredientId = Id.of("ingredient-1");
        RecipeIngredient recipeIngredient = new RecipeIngredient(ingredientId, 1.0, Unit.KG);
        List<RecipeIngredient> ingredients = List.of(recipeIngredient);
        
        Recipe recipe = new Recipe(fakeIdGenerator, "Cake Recipe", ingredients);

        RecipeDto dto = RecipeDto.from(recipe);

        assert dto.id().equals("cake-recipe-id-456");
        assert dto.name().equals("Cake Recipe");
        assert dto.ingredients().size() == 1;
        assert dto.ingredients().contains(recipeIngredient);
    }
}