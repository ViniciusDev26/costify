package br.unifor.costify.application.dto;

import br.unifor.costify.domain.valueobject.Id;
import br.unifor.costify.domain.valueobject.RecipeIngredient;
import br.unifor.costify.domain.valueobject.Unit;
import org.junit.jupiter.api.Test;

import java.util.List;

class RegisterRecipeCommandTest {

    @Test
    void createCommand_withValidValues_shouldInitializeCorrectly() {
        Id ingredientId = Id.of("ingredient-1");
        RecipeIngredient recipeIngredient = new RecipeIngredient(ingredientId, 0.5, Unit.KG);
        List<RecipeIngredient> ingredients = List.of(recipeIngredient);
        
        RegisterRecipeCommand command = new RegisterRecipeCommand("Bread Recipe", ingredients);

        assert command.name().equals("Bread Recipe");
        assert command.ingredients().size() == 1;
        assert command.ingredients().contains(recipeIngredient);
    }

    @Test
    void createCommand_withNullName_shouldThrowException() {
        Id ingredientId = Id.of("ingredient-1");
        RecipeIngredient recipeIngredient = new RecipeIngredient(ingredientId, 0.5, Unit.KG);
        List<RecipeIngredient> ingredients = List.of(recipeIngredient);
        
        try {
            new RegisterRecipeCommand(null, ingredients);
            assert false;
        } catch (IllegalArgumentException e) {
            assert e.getMessage().contains("name cannot be null or empty");
        }
    }

    @Test
    void createCommand_withEmptyName_shouldThrowException() {
        Id ingredientId = Id.of("ingredient-1");
        RecipeIngredient recipeIngredient = new RecipeIngredient(ingredientId, 0.5, Unit.KG);
        List<RecipeIngredient> ingredients = List.of(recipeIngredient);
        
        try {
            new RegisterRecipeCommand("", ingredients);
            assert false;
        } catch (IllegalArgumentException e) {
            assert e.getMessage().contains("name cannot be null or empty");
        }
    }

    @Test
    void createCommand_withBlankName_shouldThrowException() {
        Id ingredientId = Id.of("ingredient-1");
        RecipeIngredient recipeIngredient = new RecipeIngredient(ingredientId, 0.5, Unit.KG);
        List<RecipeIngredient> ingredients = List.of(recipeIngredient);
        
        try {
            new RegisterRecipeCommand("   ", ingredients);
            assert false;
        } catch (IllegalArgumentException e) {
            assert e.getMessage().contains("name cannot be null or empty");
        }
    }

    @Test
    void createCommand_withNullIngredients_shouldThrowException() {
        try {
            new RegisterRecipeCommand("Recipe Name", null);
            assert false;
        } catch (IllegalArgumentException e) {
            assert e.getMessage().contains("must have at least one ingredient");
        }
    }

    @Test
    void createCommand_withEmptyIngredients_shouldThrowException() {
        try {
            new RegisterRecipeCommand("Recipe Name", List.of());
            assert false;
        } catch (IllegalArgumentException e) {
            assert e.getMessage().contains("must have at least one ingredient");
        }
    }
}