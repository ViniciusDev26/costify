package br.unifor.costify.domain.errors.ingredient;

import br.unifor.costify.domain.errors.DomainErrorCode;
import br.unifor.costify.domain.errors.DomainException;

/**
 * Exception thrown when an ingredient name is invalid according to business rules.
 * Ingredient names must be non-blank and follow naming conventions.
 */
public class InvalidIngredientNameException extends DomainException {
    
    private static final String DEFAULT_MESSAGE = "Invalid ingredient name provided";
    
    public InvalidIngredientNameException() {
        super(DomainErrorCode.INVALID_INGREDIENT_NAME, DEFAULT_MESSAGE);
    }
    
    public InvalidIngredientNameException(String message) {
        super(DomainErrorCode.INVALID_INGREDIENT_NAME, message);
    }
    
    public InvalidIngredientNameException(String message, Throwable cause) {
        super(DomainErrorCode.INVALID_INGREDIENT_NAME, message, cause);
    }
    
    public static InvalidIngredientNameException blankName() {
        return new InvalidIngredientNameException("Ingredient name cannot be blank or empty");
    }
    
    public static InvalidIngredientNameException tooLong(String name, int maxLength) {
        return new InvalidIngredientNameException(
            String.format("Ingredient name '%s' exceeds maximum length of %d characters", name, maxLength));
    }
}