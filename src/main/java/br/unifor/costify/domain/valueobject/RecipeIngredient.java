package br.unifor.costify.domain.valueobject;

import br.unifor.costify.domain.errors.recipe.InvalidQuantityException;
import java.util.Objects;

public class RecipeIngredient {
  private final Id ingredientId;
  private final double quantity;
  private final Unit unit;

  public RecipeIngredient(Id ingredientId, double quantity, Unit unit) {
    if (quantity <= 0) {
      throw new InvalidQuantityException("Quantity must be greater than zero");
    }

    this.ingredientId = ingredientId;
    this.quantity = quantity;
    this.unit = unit;
  }

  public Id getIngredientId() {
    return ingredientId;
  }

  public double getQuantity() {
    return quantity;
  }

  public Unit getUnit() {
    return unit;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    RecipeIngredient that = (RecipeIngredient) o;
    return Double.compare(quantity, that.quantity) == 0
        && Objects.equals(ingredientId, that.ingredientId)
        && unit == that.unit;
  }

  @Override
  public int hashCode() {
    return Objects.hash(ingredientId, quantity, unit);
  }
}
