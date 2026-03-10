package br.unifor.costify.application.dto.command;

import br.unifor.costify.domain.valueobject.Unit;

public record UpdateIngredientCommand(
    String name,
    Double packageQuantity,
    Double packagePrice,
    Unit packageUnit
) {
  // All fields are optional - null means "keep existing value"
}