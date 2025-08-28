package br.unifor.costify.domain.errors.ingredient;

import br.unifor.costify.domain.errors.DomainException;

public class InvalidIngredientNameException extends DomainException {
    private static final String DEFAULT_MESSAGE = "Invalid ingredient name provided";
    
    public InvalidIngredientNameException() {
        super(DEFAULT_MESSAGE);
    }
    
    public InvalidIngredientNameException(String message) {
        super(message);
    }
}