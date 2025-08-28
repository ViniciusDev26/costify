package br.unifor.costify.domain.entity;

import br.unifor.costify.domain.contracts.IdGenerator;
import br.unifor.costify.domain.errors.money.NegativeMoneyException;
import br.unifor.costify.domain.valueobject.Id;
import br.unifor.costify.domain.valueobject.Money;
import br.unifor.costify.domain.valueobject.Unit;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

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
}
