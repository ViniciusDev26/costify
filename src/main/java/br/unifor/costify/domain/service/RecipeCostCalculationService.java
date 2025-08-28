package br.unifor.costify.domain.service;

import br.unifor.costify.domain.entity.Ingredient;
import br.unifor.costify.domain.entity.Recipe;
import br.unifor.costify.domain.valueobject.Id;
import br.unifor.costify.domain.valueobject.IngredientCost;
import br.unifor.costify.domain.valueobject.Money;
import br.unifor.costify.domain.valueobject.RecipeCost;
import br.unifor.costify.domain.valueobject.RecipeIngredient;

import java.util.List;
import java.util.Map;
import java.util.ArrayList;

public class RecipeCostCalculationService {

    public RecipeCost calculateCost(Recipe recipe, Map<Id, Ingredient> ingredientMap) {
        validateInputs(recipe, ingredientMap);

        List<IngredientCost> ingredientCosts = new ArrayList<>();

        for (RecipeIngredient recipeIngredient : recipe.getIngredients()) {
            Ingredient ingredient = ingredientMap.get(recipeIngredient.getIngredientId());
            
            if (ingredient == null) {
                throw new IllegalArgumentException(
                    "Ingredient not found with ID: " + recipeIngredient.getIngredientId());
            }

            IngredientCost ingredientCost = calculateIngredientCost(ingredient, recipeIngredient);
            ingredientCosts.add(ingredientCost);
        }

        return new RecipeCost(recipe.getId(), recipe.getName(), ingredientCosts);
    }

    private void validateInputs(Recipe recipe, Map<Id, Ingredient> ingredientMap) {
        if (recipe == null) {
            throw new IllegalArgumentException("Recipe cannot be null");
        }
        if (ingredientMap == null) {
            throw new IllegalArgumentException("Ingredient map cannot be null");
        }
    }

    private IngredientCost calculateIngredientCost(Ingredient ingredient, RecipeIngredient recipeIngredient) {
        // Use existing getUnitCost() method which already handles unit conversion properly
        double unitCost = ingredient.getUnitCost();
        
        // Convert recipe quantity to base units for calculation
        double recipeQuantityInBaseUnits = recipeIngredient.getUnit().toBase(recipeIngredient.getQuantity());
        
        // Calculate total cost for the recipe quantity
        Money totalCost = Money.of(unitCost * recipeQuantityInBaseUnits);

        return new IngredientCost(
            ingredient.getId(),
            ingredient.getName(),
            recipeIngredient.getQuantity(), // Keep original quantity
            recipeIngredient.getUnit(),     // Keep original unit
            totalCost
        );
    }

}