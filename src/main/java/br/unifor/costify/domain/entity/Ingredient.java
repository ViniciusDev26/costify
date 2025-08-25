package br.unifor.costify.domain.entity;

import br.unifor.costify.domain.contracts.IdGenerator;
import br.unifor.costify.domain.valueobject.Id;
import br.unifor.costify.domain.valueobject.Unit;
import java.util.Objects;

public class Ingredient {
  private Id id;
  private String name;
  private double packageQuantity;
  private double packagePrice;
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
      Id id, String name, double packageQuantity, double packagePrice, Unit packageUnit) {
    this.id = Objects.requireNonNull(id, "Id cannot be null");
    this.validate(name, packageQuantity, packagePrice, packageUnit);
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
      double packagePrice,
      Unit packageUnit) {
    this.validate(name, packageQuantity, packagePrice, packageUnit);
    this.id = Id.generate(idGenerator);
    this.name = name;
    this.packageQuantity = packageQuantity;
    this.packagePrice = packagePrice;
    this.packageUnit = packageUnit;
  }

  public double getUnitCost() {
    return packagePrice / packageUnit.toBase(packageQuantity);
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

  public double getPackagePrice() {
    return packagePrice;
  }

  public Unit getPackageUnit() {
    return packageUnit;
  }

  // Setters
  public void setName(String name) {
    if (name == null || name.isBlank()) {
      throw new IllegalArgumentException("Ingredient name cannot be null or empty");
    }

    this.name = name;
  }

  public void setPackageQuantity(double packageQuantity) {
    if (packageQuantity <= 0) {
      throw new IllegalArgumentException("Package quantity must be greater than zero");
    }

    this.packageQuantity = packageQuantity;
  }

  public void setPackagePrice(double packagePrice) {
    if (packagePrice < 0) {
      throw new IllegalArgumentException("Package price cannot be negative");
    }

    this.packagePrice = packagePrice;
  }

  public void setPackageUnit(Unit packageUnit) {
    if (packageUnit == null) {
      throw new IllegalArgumentException("Package unit cannot be null");
    }

    this.packageUnit = packageUnit;
  }

  private void validate(
      String name, double packageQuantity, double packagePrice, Unit packageUnit) {
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
