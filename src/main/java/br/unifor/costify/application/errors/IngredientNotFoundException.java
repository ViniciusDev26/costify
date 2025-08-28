package br.unifor.costify.application.errors;

public class IngredientNotFoundException extends RuntimeException {
    private static final String DEFAULT_MESSAGE = "Ingredient not found";
    
    public IngredientNotFoundException() {
        super(DEFAULT_MESSAGE);
    }
    
    public IngredientNotFoundException(String message) {
        super(message);
    }
}