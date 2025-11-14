package br.unifor.costify.domain.entity;

import br.unifor.costify.domain.contracts.IdGenerator;
import br.unifor.costify.domain.errors.ingredient.InvalidIngredientNameException;
import br.unifor.costify.domain.errors.money.NegativeMoneyException;
import br.unifor.costify.domain.events.DomainEvent;
import br.unifor.costify.domain.events.ingredient.IngredientUpdatedEvent;
import br.unifor.costify.domain.valueobject.Id;
import br.unifor.costify.domain.valueobject.Money;
import br.unifor.costify.domain.valueobject.Unit;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class IngredientTest {

  private IdGenerator fakeIdGenerator;

  @BeforeEach
  void setup() {
    // IdGenerator fake previsível para teste
    fakeIdGenerator = () -> "test-id-123";
  }

  @Test
  void createIngredient_withIdGenerator_shouldInitializeCorrectly() {
    Ingredient ingredient = new Ingredient(fakeIdGenerator, "Leite", 1.0, Money.of(5.0), Unit.L);

    assert ingredient.getId().getValue().equals("test-id-123");
    assert ingredient.getName().equals("Leite");
    assert ingredient.getPackageQuantity() == 1.0;
    assert ingredient.getPackagePrice().doubleValue() == 5.0;
    assert ingredient.getPackageUnit() == Unit.L;
  }

  @Test
  void createIngredient_withId_shouldInitializeCorrectly() {
    Id existingId = Id.of("existing-id");
    Ingredient ingredient = new Ingredient(existingId, "Leite", 1.0, Money.of(5.0), Unit.L);

    assert ingredient.getId().getValue().equals("existing-id");
    assert ingredient.getName().equals("Leite");
    assert ingredient.getPackageQuantity() == 1.0;
    assert ingredient.getPackagePrice().doubleValue() == 5.0;
    assert ingredient.getPackageUnit() == Unit.L;
  }

  @Test
  void createIngredient_withInvalidPackageQuantity_shouldThrowException() {
    try {
      new Ingredient(fakeIdGenerator, "Leite", 0, Money.of(5.0), Unit.L);
      assert false;
    } catch (IllegalArgumentException ignored) {
    }
  }

  @Test
  void createIngredient_withNegativePrice_shouldThrowNegativeMoneyException() {
    try {
      new Ingredient(fakeIdGenerator, "Leite", 1.0, Money.of(-1.0), Unit.L);
      assert false;
    } catch (NegativeMoneyException ignored) {
    }
  }


  @Test
  void getUnitCost_shouldReturnCorrectValue() {
    Ingredient ingredient = new Ingredient(fakeIdGenerator, "Leite", 1.0, Money.of(5.0), Unit.L);

    double expectedUnitCost = 5.0 / Unit.L.toBase(1.0);
    assert ingredient.getUnitCost() == expectedUnitCost;
  }

  @Test
  void updateIngredient_shouldUpdateAllFields() {
    // Arrange
    Id id = Id.of("test-id");
    Ingredient ingredient = new Ingredient(id, "Leite", 1.0, Money.of(5.0), Unit.L);

    // Act
    ingredient.update("Leite Desnatado", 2.0, Money.of(8.0), Unit.L);

    // Assert
    assertEquals("Leite Desnatado", ingredient.getName());
    assertEquals(2.0, ingredient.getPackageQuantity());
    assertEquals(Money.of(8.0), ingredient.getPackagePrice());
    assertEquals(Unit.L, ingredient.getPackageUnit());
  }

  @Test
  void updateIngredient_shouldEmitIngredientUpdatedEvent() {
    // Arrange
    Id id = Id.of("test-id");
    Ingredient ingredient = new Ingredient(id, "Leite", 1.0, Money.of(5.0), Unit.L);

    // Act
    ingredient.update("Leite Desnatado", 2.0, Money.of(8.0), Unit.L);
    List<DomainEvent> events = ingredient.getDomainEvents();

    // Assert
    assertFalse(events.isEmpty());
    assertEquals(1, events.size());
    assertTrue(events.get(0) instanceof IngredientUpdatedEvent);

    IngredientUpdatedEvent event = (IngredientUpdatedEvent) events.get(0);
    assertEquals(id, event.getIngredientId());
    assertEquals("Leite Desnatado", event.getNewName());
    assertEquals(2.0, event.getNewPackageQuantity());
    assertEquals(Money.of(8.0), event.getNewPackagePrice());
    assertEquals(Unit.L, event.getNewPackageUnit());
    assertEquals("IngredientUpdated", event.getEventType());
  }

  @Test
  void updateIngredient_withInvalidName_shouldThrowException() {
    // Arrange
    Id id = Id.of("test-id");
    Ingredient ingredient = new Ingredient(id, "Leite", 1.0, Money.of(5.0), Unit.L);

    // Act & Assert
    assertThrows(InvalidIngredientNameException.class, () -> {
      ingredient.update("", 2.0, Money.of(8.0), Unit.L);
    });

    assertThrows(InvalidIngredientNameException.class, () -> {
      ingredient.update(null, 2.0, Money.of(8.0), Unit.L);
    });
  }

  @Test
  void updateIngredient_withInvalidQuantity_shouldThrowException() {
    // Arrange
    Id id = Id.of("test-id");
    Ingredient ingredient = new Ingredient(id, "Leite", 1.0, Money.of(5.0), Unit.L);

    // Act & Assert
    assertThrows(IllegalArgumentException.class, () -> {
      ingredient.update("Leite", 0, Money.of(8.0), Unit.L);
    });

    assertThrows(IllegalArgumentException.class, () -> {
      ingredient.update("Leite", -1, Money.of(8.0), Unit.L);
    });
  }

  @Test
  void updateIngredient_withNegativePrice_shouldThrowException() {
    // Arrange
    Id id = Id.of("test-id");
    Ingredient ingredient = new Ingredient(id, "Leite", 1.0, Money.of(5.0), Unit.L);

    // Act & Assert
    assertThrows(NegativeMoneyException.class, () -> {
      ingredient.update("Leite", 2.0, Money.of(-1.0), Unit.L);
    });
  }

  @Test
  void clearDomainEvents_shouldRemoveAllEvents() {
    // Arrange
    Id id = Id.of("test-id");
    Ingredient ingredient = new Ingredient(id, "Leite", 1.0, Money.of(5.0), Unit.L);
    ingredient.update("Leite Desnatado", 2.0, Money.of(8.0), Unit.L);

    // Act
    ingredient.clearDomainEvents();

    // Assert
    assertTrue(ingredient.getDomainEvents().isEmpty());
  }
}
