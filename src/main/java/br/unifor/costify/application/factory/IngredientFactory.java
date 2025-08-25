package br.unifor.costify.application.factory;

import br.unifor.costify.domain.contracts.IdGenerator;
import br.unifor.costify.domain.entity.Ingredient;
import br.unifor.costify.domain.valueobject.Id;
import br.unifor.costify.domain.valueobject.Unit;
import org.springframework.stereotype.Component;

@Component
public class IngredientFactory {
  private final IdGenerator idGenerator;

  public IngredientFactory(IdGenerator idGenerator) {
    this.idGenerator = idGenerator;
  }

  public Ingredient create(
      String name, double packageQuantity, double packagePrice, Unit packageUnit) {
    return createIngredient(idGenerator, name, packageQuantity, packagePrice, packageUnit);
  }

  public Ingredient create(
      Id id, String name, double packageQuantity, double packagePrice, Unit packageUnit) {
    return createIngredient(id, name, packageQuantity, packagePrice, packageUnit);
  }

  private Ingredient createIngredient(
      IdGenerator idGenerator,
      String name,
      double packageQuantity,
      double packagePrice,
      Unit packageUnit) {
    return new Ingredient(idGenerator, name, packageQuantity, packagePrice, packageUnit);
  }

  private Ingredient createIngredient(
      Id id, String name, double packageQuantity, double packagePrice, Unit packageUnit) {
    return new Ingredient(id, name, packageQuantity, packagePrice, packageUnit);
  }
}
