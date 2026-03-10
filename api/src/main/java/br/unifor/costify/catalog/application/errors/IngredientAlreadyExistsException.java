package br.unifor.costify.catalog.application.errors;

import br.unifor.costify.shared.application.errors.ApplicationException;
import br.unifor.costify.shared.application.errors.ApplicationErrorCode;

/**
 * Exception thrown when attempting to create an ingredient that already exists in the system.
 * This prevents duplicate ingredient registration.
 */
public class IngredientAlreadyExistsException extends ApplicationException {
  
  public IngredientAlreadyExistsException(String message) {
    super(ApplicationErrorCode.INGREDIENT_ALREADY_EXISTS, message);
  }
  
  public IngredientAlreadyExistsException(String message, Throwable cause) {
    super(ApplicationErrorCode.INGREDIENT_ALREADY_EXISTS, message, cause);
  }
  
  public static IngredientAlreadyExistsException withName(String ingredientName) {
    return new IngredientAlreadyExistsException(
        String.format("Ingredient with name '%s' already exists", ingredientName));
  }
}
