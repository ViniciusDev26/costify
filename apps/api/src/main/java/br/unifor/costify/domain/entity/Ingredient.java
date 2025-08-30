package br.unifor.costify.domain.entity;

import br.unifor.costify.domain.contracts.IdGenerator;
import br.unifor.costify.domain.errors.ingredient.InvalidIngredientNameException;
import br.unifor.costify.domain.valueobject.Id;
import br.unifor.costify.domain.valueobject.Money;
import br.unifor.costify.domain.valueobject.Unit;

public class Ingredient {
  private Id id;
  private String name;
  private double packageQuantity;
  private Money packagePrice;
  private Unit packageUnit;

  /**
   * Construtor da entidade
   *
   * @param id Ingredient ID
   * @param name Ingredient name
   * @param packageQuantity total quantity in the package
   * @param packagePrice Price of the package
   * @param packageUnit Unit of measurement for the package
   */
  public Ingredient(
      Id id, String name, double packageQuantity, Money packagePrice, Unit packageUnit) {
    this.validate(name, packageQuantity);
    this.id = id;
    this.name = name;
    this.packageQuantity = packageQuantity;
    this.packagePrice = packagePrice;
    this.packageUnit = packageUnit;
  }

  /**
   * Construtor da entidade
   *
   * @param idGenerator Generator for the entity ID
   * @param name Ingredient name
   * @param packageQuantity total quantity in the package
   * @param packagePrice Price of the package
   * @param packageUnit Unit of measurement for the package
   */
  public Ingredient(
      IdGenerator idGenerator,
      String name,
      double packageQuantity,
      Money packagePrice,
      Unit packageUnit) {
    this.validate(name, packageQuantity);
    this.id = Id.generate(idGenerator);
    this.name = name;
    this.packageQuantity = packageQuantity;
    this.packagePrice = packagePrice;
    this.packageUnit = packageUnit;
  }

  public double getUnitCost() {
    return packagePrice.doubleValue() / packageUnit.toBase(packageQuantity);
  }

  // Getters
  public Id getId() {
    return id;
  }

  public String getName() {
    return name;
  }

  public double getPackageQuantity() {
    return packageQuantity;
  }

  public Money getPackagePrice() {
    return packagePrice;
  }

  public Unit getPackageUnit() {
    return packageUnit;
  }


  private void validate(String name, double packageQuantity) {
    if (name == null || name.trim().isEmpty()) {
      throw new InvalidIngredientNameException("Ingredient name cannot be null or empty");
    }
    if (packageQuantity <= 0) {
      throw new IllegalArgumentException("Package quantity must be greater than zero");
    }
  }
}
