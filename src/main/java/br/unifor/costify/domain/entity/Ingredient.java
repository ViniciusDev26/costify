package br.unifor.costify.domain.entity;

import br.unifor.costify.domain.contracts.IdGenerator;
import br.unifor.costify.domain.errors.ingredient.InvalidIngredientNameException;
import br.unifor.costify.domain.events.DomainEvent;
import br.unifor.costify.domain.events.ingredient.IngredientUpdatedEvent;
import br.unifor.costify.domain.valueobject.Id;
import br.unifor.costify.domain.valueobject.Money;
import br.unifor.costify.domain.valueobject.Unit;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Ingredient {
  private Id id;
  private String name;
  private double packageQuantity;
  private Money packagePrice;
  private Unit packageUnit;
  private final List<DomainEvent> domainEvents = new ArrayList<>();

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


  /**
   * Update the ingredient with new values and emit a domain event.
   *
   * @param name New ingredient name
   * @param packageQuantity New package quantity
   * @param packagePrice New package price
   * @param packageUnit New package unit
   */
  public void update(String name, double packageQuantity, Money packagePrice, Unit packageUnit) {
    this.validate(name, packageQuantity);
    this.name = name;
    this.packageQuantity = packageQuantity;
    this.packagePrice = packagePrice;
    this.packageUnit = packageUnit;

    // Emit domain event
    this.domainEvents.add(new IngredientUpdatedEvent(
        this.id,
        name,
        packageQuantity,
        packagePrice,
        packageUnit
    ));
  }

  /**
   * Get all domain events emitted by this entity.
   *
   * @return Unmodifiable list of domain events
   */
  public List<DomainEvent> getDomainEvents() {
    return Collections.unmodifiableList(domainEvents);
  }

  /**
   * Clear all domain events.
   * This should be called after events have been published.
   */
  public void clearDomainEvents() {
    this.domainEvents.clear();
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
