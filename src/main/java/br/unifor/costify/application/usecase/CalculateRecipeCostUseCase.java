package br.unifor.costify.application.usecase;

import br.unifor.costify.application.contracts.IngredientRepository;
import br.unifor.costify.application.contracts.RecipeRepository;
import br.unifor.costify.application.dto.response.RecipeCostDto;
import br.unifor.costify.application.errors.IngredientNotFoundException;
import br.unifor.costify.application.errors.RecipeNotFoundException;
import br.unifor.costify.domain.entity.Ingredient;
import br.unifor.costify.domain.entity.Recipe;
import br.unifor.costify.domain.service.RecipeCostCalculationService;
import br.unifor.costify.domain.valueobject.Id;
import br.unifor.costify.domain.valueobject.RecipeCost;
import br.unifor.costify.domain.valueobject.RecipeIngredient;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class CalculateRecipeCostUseCase {
    
    private final RecipeRepository recipeRepository;
    private final IngredientRepository ingredientRepository;
    private final RecipeCostCalculationService costCalculationService;
    
    public CalculateRecipeCostUseCase(
            RecipeRepository recipeRepository,
            IngredientRepository ingredientRepository,
            RecipeCostCalculationService costCalculationService) {
        this.recipeRepository = recipeRepository;
        this.ingredientRepository = ingredientRepository;
        this.costCalculationService = costCalculationService;
    }
    
    public RecipeCostDto execute(String recipeId) {
        Id id = Id.of(recipeId);
        
        Optional<Recipe> recipeOpt = recipeRepository.findById(id);
        if (recipeOpt.isEmpty()) {
            throw new RecipeNotFoundException("Recipe not found with ID: " + recipeId);
        }
        
        Recipe recipe = recipeOpt.get();
        Map<Id, Ingredient> ingredientMap = loadIngredients(recipe.getIngredients());
        
        RecipeCost recipeCost = costCalculationService.calculateCost(recipe, ingredientMap);
        
        return RecipeCostDto.fromDomain(recipeCost);
    }
    
    private Map<Id, Ingredient> loadIngredients(List<RecipeIngredient> recipeIngredients) {
        Map<Id, Ingredient> ingredientMap = new HashMap<>();
        
        for (RecipeIngredient recipeIngredient : recipeIngredients) {
            Id ingredientId = recipeIngredient.getIngredientId();
            
            Optional<Ingredient> ingredientOpt = ingredientRepository.findById(ingredientId);
            if (ingredientOpt.isEmpty()) {
                throw new IngredientNotFoundException("Ingredient not found with ID: " + ingredientId);
            }
            
            ingredientMap.put(ingredientId, ingredientOpt.get());
        }
        
        return ingredientMap;
    }
}