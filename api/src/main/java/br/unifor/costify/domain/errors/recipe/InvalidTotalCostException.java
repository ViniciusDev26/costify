package br.unifor.costify.domain.errors.recipe;

import br.unifor.costify.domain.errors.DomainErrorCode;
import br.unifor.costify.domain.errors.DomainException;

/**
 * Exception thrown when an invalid total cost is calculated or provided for a recipe.
 * Total costs must be non-negative and match the sum of ingredient costs.
 */
public class InvalidTotalCostException extends DomainException {
    
    private static final String DEFAULT_MESSAGE = "Invalid recipe total cost";
    
    public InvalidTotalCostException() {
        super(DomainErrorCode.INVALID_TOTAL_COST, DEFAULT_MESSAGE);
    }
    
    public InvalidTotalCostException(String message) {
        super(DomainErrorCode.INVALID_TOTAL_COST, message);
    }
    
    public InvalidTotalCostException(String message, Throwable cause) {
        super(DomainErrorCode.INVALID_TOTAL_COST, message, cause);
    }
    
    public static InvalidTotalCostException negative(double totalCost) {
        return new InvalidTotalCostException(
            String.format("Recipe total cost cannot be negative: %.2f", totalCost));
    }
    
    public static InvalidTotalCostException mismatch(double expectedCost, double actualCost) {
        return new InvalidTotalCostException(
            String.format("Recipe total cost mismatch. Expected: %.2f, Actual: %.2f", 
                expectedCost, actualCost));
    }
    
    public static InvalidTotalCostException calculationError(String details) {
        return new InvalidTotalCostException(
            String.format("Error calculating recipe total cost: %s", details));
    }
}