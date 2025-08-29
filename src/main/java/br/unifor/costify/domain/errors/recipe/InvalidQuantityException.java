package br.unifor.costify.domain.errors.recipe;

import br.unifor.costify.domain.errors.DomainErrorCode;
import br.unifor.costify.domain.errors.DomainException;

/**
 * Exception thrown when an invalid quantity is specified for a recipe ingredient.
 * Quantities must be positive and within reasonable bounds.
 */
public class InvalidQuantityException extends DomainException {
    
    private static final String DEFAULT_MESSAGE = "Invalid quantity provided";
    
    public InvalidQuantityException() {
        super(DomainErrorCode.INVALID_QUANTITY, DEFAULT_MESSAGE);
    }
    
    public InvalidQuantityException(String message) {
        super(DomainErrorCode.INVALID_QUANTITY, message);
    }
    
    public InvalidQuantityException(String message, Throwable cause) {
        super(DomainErrorCode.INVALID_QUANTITY, message, cause);
    }
    
    public static InvalidQuantityException negative(double quantity) {
        return new InvalidQuantityException(
            String.format("Recipe ingredient quantity cannot be negative: %.2f", quantity));
    }
    
    public static InvalidQuantityException zero() {
        return new InvalidQuantityException(
            "Recipe ingredient quantity must be greater than zero");
    }
    
    public static InvalidQuantityException tooLarge(double quantity, double maxQuantity) {
        return new InvalidQuantityException(
            String.format("Recipe ingredient quantity %.2f exceeds maximum allowed: %.2f", 
                quantity, maxQuantity));
    }
}