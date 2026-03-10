package br.unifor.costify.domain.events.ingredient;

import br.unifor.costify.domain.events.DomainEvent;
import br.unifor.costify.domain.valueobject.Id;
import br.unifor.costify.domain.valueobject.Money;
import br.unifor.costify.domain.valueobject.Unit;

import java.time.Instant;

/**
 * Domain event emitted when an ingredient is updated.
 * This event captures the new state of the ingredient after the update.
 */
public class IngredientUpdatedEvent implements DomainEvent {
  private final Id ingredientId;
  private final String newName;
  private final double newPackageQuantity;
  private final Money newPackagePrice;
  private final Unit newPackageUnit;
  private final Instant occurredOn;

  public IngredientUpdatedEvent(
      Id ingredientId,
      String newName,
      double newPackageQuantity,
      Money newPackagePrice,
      Unit newPackageUnit) {
    this.ingredientId = ingredientId;
    this.newName = newName;
    this.newPackageQuantity = newPackageQuantity;
    this.newPackagePrice = newPackagePrice;
    this.newPackageUnit = newPackageUnit;
    this.occurredOn = Instant.now();
  }

  @Override
  public Instant getOccurredOn() {
    return occurredOn;
  }

  @Override
  public String getEventType() {
    return "IngredientUpdated";
  }

  public Id getIngredientId() {
    return ingredientId;
  }

  public String getNewName() {
    return newName;
  }

  public double getNewPackageQuantity() {
    return newPackageQuantity;
  }

  public Money getNewPackagePrice() {
    return newPackagePrice;
  }

  public Unit getNewPackageUnit() {
    return newPackageUnit;
  }
}
