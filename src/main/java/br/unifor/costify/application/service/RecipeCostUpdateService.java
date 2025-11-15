package br.unifor.costify.application.service;

import br.unifor.costify.application.contracts.IngredientRepository;
import br.unifor.costify.application.contracts.RecipeRepository;
import br.unifor.costify.domain.entity.Ingredient;
import br.unifor.costify.domain.entity.Recipe;
import br.unifor.costify.domain.service.RecipeCostCalculationService;
import br.unifor.costify.domain.valueobject.Id;
import br.unifor.costify.domain.valueobject.RecipeCost;
import br.unifor.costify.domain.valueobject.RecipeIngredient;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Service responsible for updating recipe costs when ingredients are modified.
 * This service is typically triggered by ingredient update events to ensure
 * all affected recipes reflect the current ingredient prices.
 */
@Service
public class RecipeCostUpdateService {

    private final RecipeRepository recipeRepository;
    private final IngredientRepository ingredientRepository;
    private final RecipeCostCalculationService costCalculationService;

    public RecipeCostUpdateService(
            RecipeRepository recipeRepository,
            IngredientRepository ingredientRepository,
            RecipeCostCalculationService costCalculationService) {
        this.recipeRepository = recipeRepository;
        this.ingredientRepository = ingredientRepository;
        this.costCalculationService = costCalculationService;
    }

    /**
     * Updates the total cost of all recipes that contain the specified ingredient.
     * This method:
     * 1. Finds all recipes using the given ingredient
     * 2. Loads all ingredients for each recipe
     * 3. Recalculates the recipe cost
     * 4. Updates and saves the recipe with the new total cost
     *
     * @param ingredientId the ID of the updated ingredient
     */
    public void updateRecipeCostsForIngredient(Id ingredientId) {
        // Find all recipes that use this ingredient
        List<Recipe> affectedRecipes = recipeRepository.findByIngredientId(ingredientId);

        // If no recipes use this ingredient, nothing to update
        if (affectedRecipes.isEmpty()) {
            return;
        }

        // Update cost for each affected recipe
        for (Recipe recipe : affectedRecipes) {
            updateRecipeCost(recipe);
        }
    }

    /**
     * Updates the cost of a single recipe by recalculating based on current ingredient prices.
     *
     * @param recipe the recipe to update
     */
    private void updateRecipeCost(Recipe recipe) {
        // Load all ingredients for this recipe
        Map<Id, Ingredient> ingredientMap = loadIngredientsForRecipe(recipe);

        // Calculate the new cost
        RecipeCost recipeCost = costCalculationService.calculateCost(recipe, ingredientMap);

        // Update the recipe with the new total cost
        recipe.updateTotalCost(recipeCost.getTotalCost());

        // Save the updated recipe
        recipeRepository.save(recipe);
    }

    /**
     * Loads all ingredients referenced in a recipe into a map.
     *
     * @param recipe the recipe whose ingredients to load
     * @return a map of ingredient IDs to Ingredient entities
     */
    private Map<Id, Ingredient> loadIngredientsForRecipe(Recipe recipe) {
        Map<Id, Ingredient> ingredientMap = new HashMap<>();

        for (RecipeIngredient recipeIngredient : recipe.getIngredients()) {
            Id ingredientId = recipeIngredient.getIngredientId();
            Optional<Ingredient> ingredientOpt = ingredientRepository.findById(ingredientId);

            ingredientOpt.ifPresent(ingredient ->
                    ingredientMap.put(ingredientId, ingredient)
            );
        }

        return ingredientMap;
    }
}
