package br.unifor.costify.domain.errors.recipe;

import br.unifor.costify.domain.errors.DomainException;

public class EmptyRecipeException extends DomainException {
    private static final String DEFAULT_MESSAGE = "Recipe cannot be empty";
    
    public EmptyRecipeException() {
        super(DEFAULT_MESSAGE);
    }
    
    public EmptyRecipeException(String message) {
        super(message);
    }
}