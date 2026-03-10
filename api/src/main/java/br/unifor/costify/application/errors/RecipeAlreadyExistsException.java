package br.unifor.costify.application.errors;

/**
 * Exception thrown when attempting to create a recipe that already exists in the system.
 * This prevents duplicate recipe registration.
 */
public class RecipeAlreadyExistsException extends ApplicationException {
  
  public RecipeAlreadyExistsException(String message) {
    super(ApplicationErrorCode.RECIPE_ALREADY_EXISTS, message);
  }
  
  public RecipeAlreadyExistsException(String message, Throwable cause) {
    super(ApplicationErrorCode.RECIPE_ALREADY_EXISTS, message, cause);
  }
  
  public static RecipeAlreadyExistsException withName(String recipeName) {
    return new RecipeAlreadyExistsException(
        String.format("Recipe with name '%s' already exists", recipeName));
  }
}
