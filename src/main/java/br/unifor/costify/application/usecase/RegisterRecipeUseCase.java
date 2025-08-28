package br.unifor.costify.application.usecase;

import br.unifor.costify.application.contracts.RecipeRepository;
import br.unifor.costify.application.dto.command.RegisterRecipeCommand;
import br.unifor.costify.application.dto.entity.RecipeDto;
import br.unifor.costify.application.errors.RecipeAlreadyExistsException;
import br.unifor.costify.application.factory.RecipeFactory;
import br.unifor.costify.application.validation.ValidationService;
import br.unifor.costify.domain.entity.Recipe;

public class RegisterRecipeUseCase {
  private final RecipeRepository recipeRepository;
  private final RecipeFactory recipeFactory;
  private final ValidationService validationService;

  public RegisterRecipeUseCase(
      RecipeRepository recipeRepository, 
      RecipeFactory recipeFactory,
      ValidationService validationService) {
    this.recipeRepository = recipeRepository;
    this.recipeFactory = recipeFactory;
    this.validationService = validationService;
  }

  public RecipeDto execute(RegisterRecipeCommand command) {
    validationService.validateRecipeData(command.name(), command.ingredients());
    
    validateRecipeDoesNotExist(command.name());

    Recipe recipe = recipeFactory.create(command.name(), command.ingredients());

    Recipe savedRecipe = recipeRepository.save(recipe);

    return RecipeDto.from(savedRecipe);
  }

  private void validateRecipeDoesNotExist(String name) {
    if (recipeRepository.existsByName(name)) {
      throw new RecipeAlreadyExistsException("Recipe with name '" + name + "' already exists");
    }
  }
}
