package br.unifor.costify.application.usecase;

import br.unifor.costify.application.contracts.RecipeRepository;
import br.unifor.costify.application.dto.response.RecipeCostDto;
import br.unifor.costify.application.errors.RecipeNotFoundException;
import br.unifor.costify.application.service.IngredientLoaderService;
import br.unifor.costify.domain.entity.Ingredient;
import br.unifor.costify.domain.entity.Recipe;
import br.unifor.costify.domain.service.RecipeCostCalculationService;
import br.unifor.costify.domain.valueobject.Id;
import br.unifor.costify.domain.valueobject.RecipeCost;
import br.unifor.costify.domain.valueobject.RecipeIngredient;

import java.util.Map;
import java.util.Optional;

public class CalculateRecipeCostUseCase {
    
    private final RecipeRepository recipeRepository;
    private final IngredientLoaderService ingredientLoaderService;
    private final RecipeCostCalculationService costCalculationService;
    
    public CalculateRecipeCostUseCase(
            RecipeRepository recipeRepository,
            IngredientLoaderService ingredientLoaderService,
            RecipeCostCalculationService costCalculationService) {
        this.recipeRepository = recipeRepository;
        this.ingredientLoaderService = ingredientLoaderService;
        this.costCalculationService = costCalculationService;
    }
    
    public RecipeCostDto execute(String recipeId) {
        Id id = Id.of(recipeId);
        
        Optional<Recipe> recipeOpt = recipeRepository.findById(id);
        if (recipeOpt.isEmpty()) {
            throw new RecipeNotFoundException("Recipe not found with ID: " + recipeId);
        }
        
        Recipe recipe = recipeOpt.get();
        Map<Id, Ingredient> ingredientMap = ingredientLoaderService.loadIngredients(recipe.getIngredients());
        
        RecipeCost recipeCost = costCalculationService.calculateCost(recipe, ingredientMap);
        
        return RecipeCostDto.fromDomain(recipeCost);
    }
}