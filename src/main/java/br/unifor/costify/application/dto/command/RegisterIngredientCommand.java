package br.unifor.costify.application.dto.command;

import br.unifor.costify.domain.valueobject.Unit;

public record RegisterIngredientCommand(
    String name,
    double packageQuantity,
    double packagePrice,
    Unit packageUnit
) {
    
    public RegisterIngredientCommand {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Ingredient name cannot be null or empty");
        }
        if (packageQuantity <= 0) {
            throw new IllegalArgumentException("Package quantity must be greater than zero");
        }
        if (packagePrice < 0) {
            throw new IllegalArgumentException("Package price cannot be negative");
        }
        if (packageUnit == null) {
            throw new IllegalArgumentException("Package unit cannot be null");
        }
    }
}