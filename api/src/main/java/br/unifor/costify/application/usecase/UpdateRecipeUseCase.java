package br.unifor.costify.application.usecase;

import br.unifor.costify.application.contracts.IngredientRepository;
import br.unifor.costify.application.contracts.RecipeRepository;
import br.unifor.costify.application.dto.command.UpdateRecipeCommand;
import br.unifor.costify.application.dto.entity.RecipeDto;
import br.unifor.costify.application.errors.RecipeNotFoundException;
import br.unifor.costify.application.service.IngredientLoaderService;
import br.unifor.costify.domain.entity.Ingredient;
import br.unifor.costify.domain.service.RecipeCostCalculationService;
import br.unifor.costify.domain.valueobject.Id;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UpdateRecipeUseCase {

    private final RecipeRepository recipeRepository;
    private final IngredientRepository ingredientRepository;
    private final RecipeCostCalculationService costCalculationService;

    @Transactional
    public RecipeDto execute(Id recipeId, UpdateRecipeCommand command) {
        // Find existing recipe
        var recipe = recipeRepository.findById(recipeId)
                .orElseThrow(() -> RecipeNotFoundException.withId(recipeId.getValue()));

        // Update recipe fields
        recipe.updateName(command.name());
        recipe.updateIngredients(command.ingredients());

        // Load ingredients for cost calculation
        var ingredientMap = command.ingredients().stream()
                .map(ri -> ingredientRepository.findById(ri.getIngredientId())
                        .orElseThrow(() -> new RuntimeException("Ingredient not found: " + ri.getIngredientId())))
                .collect(java.util.stream.Collectors.toMap(
                        Ingredient::getId,
                        ingredient -> ingredient
                ));

        // Recalculate cost
        var recipeCost = costCalculationService.calculateCost(recipe, ingredientMap);
        recipe.updateTotalCost(recipeCost.getTotalCost());

        // Save and return
        var savedRecipe = recipeRepository.save(recipe);
        return RecipeDto.from(savedRecipe);
    }
}
