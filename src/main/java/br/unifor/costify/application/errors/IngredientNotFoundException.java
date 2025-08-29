package br.unifor.costify.application.errors;

/**
 * Exception thrown when a requested ingredient cannot be found in the system.
 * This typically occurs during use case execution when referencing non-existent ingredients.
 */
public class IngredientNotFoundException extends ApplicationException {
    
    private static final String DEFAULT_MESSAGE = "Ingredient not found";
    
    public IngredientNotFoundException() {
        super(ApplicationErrorCode.INGREDIENT_NOT_FOUND, DEFAULT_MESSAGE);
    }
    
    public IngredientNotFoundException(String message) {
        super(ApplicationErrorCode.INGREDIENT_NOT_FOUND, message);
    }
    
    public IngredientNotFoundException(String message, Throwable cause) {
        super(ApplicationErrorCode.INGREDIENT_NOT_FOUND, message, cause);
    }
    
    public static IngredientNotFoundException withId(String ingredientId) {
        return new IngredientNotFoundException(
            String.format("Ingredient with ID '%s' not found", ingredientId));
    }
    
    public static IngredientNotFoundException withName(String ingredientName) {
        return new IngredientNotFoundException(
            String.format("Ingredient with name '%s' not found", ingredientName));
    }
}