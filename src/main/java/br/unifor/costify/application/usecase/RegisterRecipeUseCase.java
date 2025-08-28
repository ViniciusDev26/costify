package br.unifor.costify.application.usecase;

import br.unifor.costify.application.contracts.RecipeRepository;
import br.unifor.costify.application.dto.command.RegisterRecipeCommand;
import br.unifor.costify.application.dto.entity.RecipeDto;
import br.unifor.costify.application.errors.RecipeAlreadyExistsException;
import br.unifor.costify.application.factory.RecipeFactory;
import br.unifor.costify.application.service.IngredientLoaderService;
import br.unifor.costify.application.validation.ValidationService;
import br.unifor.costify.domain.entity.Ingredient;
import br.unifor.costify.domain.entity.Recipe;
import br.unifor.costify.domain.service.RecipeCostCalculationService;
import br.unifor.costify.domain.valueobject.Id;
import br.unifor.costify.domain.valueobject.Money;
import br.unifor.costify.domain.valueobject.RecipeCost;
import br.unifor.costify.domain.valueobject.RecipeIngredient;

import java.util.Map;

public class RegisterRecipeUseCase {
  private final RecipeRepository recipeRepository;
  private final IngredientLoaderService ingredientLoaderService;
  private final RecipeFactory recipeFactory;
  private final ValidationService validationService;
  private final RecipeCostCalculationService costCalculationService;

  public RegisterRecipeUseCase(
      RecipeRepository recipeRepository,
      IngredientLoaderService ingredientLoaderService,
      RecipeFactory recipeFactory,
      ValidationService validationService,
      RecipeCostCalculationService costCalculationService) {
    this.recipeRepository = recipeRepository;
    this.ingredientLoaderService = ingredientLoaderService;
    this.recipeFactory = recipeFactory;
    this.validationService = validationService;
    this.costCalculationService = costCalculationService;
  }

  public RecipeDto execute(RegisterRecipeCommand command) {
    validationService.validateRecipeData(command.name(), command.ingredients());
    
    validateRecipeDoesNotExist(command.name());

    // Load ingredients and calculate cost
    Map<Id, Ingredient> ingredientMap = ingredientLoaderService.loadIngredients(command.ingredients());
    
    // Create temporary recipe with zero cost for cost calculation
    Recipe tempRecipe = recipeFactory.create(command.name(), command.ingredients(), Money.zero());
    RecipeCost recipeCost = costCalculationService.calculateCost(tempRecipe, ingredientMap);
    
    // Create final recipe with calculated total cost
    Recipe recipe = recipeFactory.create(command.name(), command.ingredients(), recipeCost.getTotalCost());

    Recipe savedRecipe = recipeRepository.save(recipe);

    return RecipeDto.from(savedRecipe);
  }

  private void validateRecipeDoesNotExist(String name) {
    if (recipeRepository.existsByName(name)) {
      throw new RecipeAlreadyExistsException("Recipe with name '" + name + "' already exists");
    }
  }

}
