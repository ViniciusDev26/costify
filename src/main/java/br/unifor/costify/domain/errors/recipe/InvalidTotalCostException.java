package br.unifor.costify.domain.errors.recipe;

import br.unifor.costify.domain.errors.DomainException;

public class InvalidTotalCostException extends DomainException {
    private static final String DEFAULT_MESSAGE = "Invalid total cost provided";
    
    public InvalidTotalCostException() {
        super(DEFAULT_MESSAGE);
    }
    
    public InvalidTotalCostException(String message) {
        super(message);
    }
}