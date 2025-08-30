package br.unifor.costify.domain.errors.recipe;

import br.unifor.costify.domain.errors.DomainErrorCode;
import br.unifor.costify.domain.errors.DomainException;

/**
 * Exception thrown when attempting to create a recipe without any ingredients.
 * All recipes must contain at least one ingredient.
 */
public class EmptyRecipeException extends DomainException {
    
    private static final String DEFAULT_MESSAGE = "Recipe must have at least one ingredient";
    
    public EmptyRecipeException() {
        super(DomainErrorCode.EMPTY_RECIPE, DEFAULT_MESSAGE);
    }
    
    public EmptyRecipeException(String message) {
        super(DomainErrorCode.EMPTY_RECIPE, message);
    }
    
    public EmptyRecipeException(String message, Throwable cause) {
        super(DomainErrorCode.EMPTY_RECIPE, message, cause);
    }
    
    public static EmptyRecipeException forRecipe(String recipeName) {
        return new EmptyRecipeException(
            String.format("Recipe '%s' must have at least one ingredient", recipeName));
    }
}