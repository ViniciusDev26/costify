package br.unifor.costify.infra.errors;

/**
 * Represents a single field validation error
 */
public record ValidationError(
    String field,
    Object rejectedValue,
    String message) {

    public static ValidationError of(String field, Object rejectedValue, String message) {
        return new ValidationError(field, rejectedValue, message);
    }
}