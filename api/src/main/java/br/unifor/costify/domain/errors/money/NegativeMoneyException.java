package br.unifor.costify.domain.errors.money;

import br.unifor.costify.domain.errors.DomainErrorCode;
import br.unifor.costify.domain.errors.DomainException;

/**
 * Exception thrown when attempting to create or use a negative money amount.
 * Money values in the system must always be non-negative.
 */
public class NegativeMoneyException extends DomainException {
    
    private static final String DEFAULT_MESSAGE = "Money amount cannot be negative";
    
    public NegativeMoneyException() {
        super(DomainErrorCode.NEGATIVE_MONEY, DEFAULT_MESSAGE);
    }
    
    public NegativeMoneyException(String message) {
        super(DomainErrorCode.NEGATIVE_MONEY, message);
    }
    
    public NegativeMoneyException(String message, Throwable cause) {
        super(DomainErrorCode.NEGATIVE_MONEY, message, cause);
    }
    
    public static NegativeMoneyException withAmount(double amount) {
        return new NegativeMoneyException(
            String.format("Money amount cannot be negative: %.2f", amount));
    }
    
    public static NegativeMoneyException withContext(double amount, String context) {
        return new NegativeMoneyException(
            String.format("Money amount cannot be negative in %s: %.2f", context, amount));
    }
}