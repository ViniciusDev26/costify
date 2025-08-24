package br.unifor.costify.application.usecase;

import br.unifor.costify.application.dto.entity.RecipeDto;
import br.unifor.costify.application.dto.command.RegisterRecipeCommand;
import br.unifor.costify.application.contracts.RecipeRepository;
import br.unifor.costify.application.factory.RecipeFactory;
import br.unifor.costify.domain.entity.Recipe;

public class RegisterRecipeUseCase {
    
    private final RecipeRepository recipeRepository;
    private final RecipeFactory recipeFactory;
    
    public RegisterRecipeUseCase(RecipeRepository recipeRepository, RecipeFactory recipeFactory) {
        this.recipeRepository = recipeRepository;
        this.recipeFactory = recipeFactory;
    }
    
    public RecipeDto execute(RegisterRecipeCommand command) {
        validateRecipeDoesNotExist(command.name());
        
        Recipe recipe = recipeFactory.create(
            command.name(),
            command.ingredients()
        );
        
        Recipe savedRecipe = recipeRepository.save(recipe);
        
        return RecipeDto.from(savedRecipe);
    }
    
    private void validateRecipeDoesNotExist(String name) {
        if (recipeRepository.existsByName(name)) {
            throw new RecipeAlreadyExistsException("Recipe with name '" + name + "' already exists");
        }
    }
    
    public static class RecipeAlreadyExistsException extends RuntimeException {
        public RecipeAlreadyExistsException(String message) {
            super(message);
        }
    }
}