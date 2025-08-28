package br.unifor.costify.domain.errors.recipe;

public class EmptyRecipeException extends RuntimeException {
    public EmptyRecipeException(String message) {
        super(message);
    }
}