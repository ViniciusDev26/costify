package br.unifor.costify.application.errors;

public class RecipeNotFoundException extends RuntimeException {
    private static final String DEFAULT_MESSAGE = "Recipe not found";
    
    public RecipeNotFoundException() {
        super(DEFAULT_MESSAGE);
    }
    
    public RecipeNotFoundException(String message) {
        super(message);
    }
}