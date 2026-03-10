package br.unifor.costify.domain.events.ingredient;

import br.unifor.costify.domain.valueobject.Id;
import br.unifor.costify.domain.valueobject.Money;
import br.unifor.costify.domain.valueobject.Unit;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;

class IngredientUpdatedEventTest {

  @Test
  void shouldCreateIngredientUpdatedEventWithAllFields() {
    // Arrange
    Id ingredientId = Id.of("550e8400-e29b-41d4-a716-446655440000");
    String newName = "Updated Milk";
    double newPackageQuantity = 2.0;
    Money newPackagePrice = Money.of(10.00);
    Unit newPackageUnit = Unit.L;
    Instant beforeEvent = Instant.now();

    // Act
    IngredientUpdatedEvent event = new IngredientUpdatedEvent(
        ingredientId,
        newName,
        newPackageQuantity,
        newPackagePrice,
        newPackageUnit
    );

    Instant afterEvent = Instant.now();

    // Assert
    assertNotNull(event);
    assertEquals(ingredientId, event.getIngredientId());
    assertEquals(newName, event.getNewName());
    assertEquals(newPackageQuantity, event.getNewPackageQuantity());
    assertEquals(newPackagePrice, event.getNewPackagePrice());
    assertEquals(newPackageUnit, event.getNewPackageUnit());
    assertNotNull(event.getOccurredOn());
    assertTrue(event.getOccurredOn().isAfter(beforeEvent) || event.getOccurredOn().equals(beforeEvent));
    assertTrue(event.getOccurredOn().isBefore(afterEvent) || event.getOccurredOn().equals(afterEvent));
  }

  @Test
  void shouldHaveEventType() {
    // Arrange
    Id ingredientId = Id.of("550e8400-e29b-41d4-a716-446655440000");
    IngredientUpdatedEvent event = new IngredientUpdatedEvent(
        ingredientId,
        "Milk",
        1.0,
        Money.of(5.00),
        Unit.L
    );

    // Act & Assert
    assertEquals("IngredientUpdated", event.getEventType());
  }

  @Test
  void shouldBeImmutable() {
    // Arrange
    Id ingredientId = Id.of("550e8400-e29b-41d4-a716-446655440000");
    String name = "Milk";
    double quantity = 1.0;
    Money price = Money.of(5.00);
    Unit unit = Unit.L;

    // Act
    IngredientUpdatedEvent event = new IngredientUpdatedEvent(
        ingredientId,
        name,
        quantity,
        price,
        unit
    );

    // Assert - All getters should return the same values
    assertEquals(ingredientId, event.getIngredientId());
    assertEquals(name, event.getNewName());
    assertEquals(quantity, event.getNewPackageQuantity());
    assertEquals(price, event.getNewPackagePrice());
    assertEquals(unit, event.getNewPackageUnit());
  }
}
