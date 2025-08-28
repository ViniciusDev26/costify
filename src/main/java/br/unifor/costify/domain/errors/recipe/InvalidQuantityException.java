package br.unifor.costify.domain.errors.recipe;

import br.unifor.costify.domain.errors.DomainException;

public class InvalidQuantityException extends DomainException {
    private static final String DEFAULT_MESSAGE = "Invalid quantity provided";
    
    public InvalidQuantityException() {
        super(DEFAULT_MESSAGE);
    }
    
    public InvalidQuantityException(String message) {
        super(message);
    }
}