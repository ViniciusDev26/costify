package br.unifor.costify.application.errors;

/**
 * Exception thrown when a requested recipe cannot be found in the system.
 * This typically occurs during use case execution when referencing non-existent recipes.
 */
public class RecipeNotFoundException extends ApplicationException {
    
    private static final String DEFAULT_MESSAGE = "Recipe not found";
    
    public RecipeNotFoundException() {
        super(ApplicationErrorCode.RECIPE_NOT_FOUND, DEFAULT_MESSAGE);
    }
    
    public RecipeNotFoundException(String message) {
        super(ApplicationErrorCode.RECIPE_NOT_FOUND, message);
    }
    
    public RecipeNotFoundException(String message, Throwable cause) {
        super(ApplicationErrorCode.RECIPE_NOT_FOUND, message, cause);
    }
    
    public static RecipeNotFoundException withId(String recipeId) {
        return new RecipeNotFoundException(
            String.format("Recipe with ID '%s' not found", recipeId));
    }
    
    public static RecipeNotFoundException withName(String recipeName) {
        return new RecipeNotFoundException(
            String.format("Recipe with name '%s' not found", recipeName));
    }
}