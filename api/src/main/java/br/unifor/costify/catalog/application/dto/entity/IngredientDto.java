package br.unifor.costify.catalog.application.dto.entity;

import br.unifor.costify.catalog.domain.entity.Ingredient;
import br.unifor.costify.shared.domain.valueobject.Unit;

public record IngredientDto(
    String id,
    String name,
    double packageQuantity,
    double packagePrice,
    Unit packageUnit,
    double unitCost) {

  public static IngredientDto from(Ingredient ingredient) {
    return new IngredientDto(
        ingredient.getId().getValue(),
        ingredient.getName(),
        ingredient.getPackageQuantity(),
        ingredient.getPackagePrice().doubleValue(),
        ingredient.getPackageUnit(),
        ingredient.getUnitCost());
  }
}
