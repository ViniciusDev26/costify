package br.unifor.costify.domain.errors.money;

import br.unifor.costify.domain.errors.DomainException;

public class NegativeMoneyException extends DomainException {
    private static final String DEFAULT_MESSAGE = "Money amount cannot be negative";
    
    public NegativeMoneyException() {
        super(DEFAULT_MESSAGE);
    }
    
    public NegativeMoneyException(String message) {
        super(message);
    }
}