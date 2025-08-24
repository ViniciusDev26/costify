package br.unifor.costify.domain.entity;

import br.unifor.costify.domain.contracts.IdGenerator;
import br.unifor.costify.domain.valueobject.Id;
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
        Ingredient ingredient = new Ingredient(fakeIdGenerator, "Leite", 1.0, 5.0, Unit.L);

        assert ingredient.getId().getValue().equals("test-id-123");
        assert ingredient.getName().equals("Leite");
        assert ingredient.getPackageQuantity() == 1.0;
        assert ingredient.getPackagePrice() == 5.0;
        assert ingredient.getPackageUnit() == Unit.L;
    }

    @Test
    void createIngredient_withId_shouldInitializeCorrectly() {
        Id existingId = Id.of("existing-id");
        Ingredient ingredient = new Ingredient(existingId, "Leite", 1.0, 5.0, Unit.L);

        assert ingredient.getId().getValue().equals("existing-id");
        assert ingredient.getName().equals("Leite");
        assert ingredient.getPackageQuantity() == 1.0;
        assert ingredient.getPackagePrice() == 5.0;
        assert ingredient.getPackageUnit() == Unit.L;
    }

    @Test
    void createIngredient_withInvalidValues_shouldThrowException() {
      try { new Ingredient(fakeIdGenerator, null, 1.0, 5.0, Unit.L); assert false; } catch (IllegalArgumentException ignored) {}
      try { new Ingredient(fakeIdGenerator, "", 1.0, 5.0, Unit.L); assert false; } catch (IllegalArgumentException ignored) {}

      try { new Ingredient(fakeIdGenerator, "Leite", 0, 5.0, Unit.L); assert false; } catch (IllegalArgumentException ignored) {}

      try { new Ingredient(fakeIdGenerator, "Leite", 1.0, -1.0, Unit.L); assert false; } catch (IllegalArgumentException ignored) {}

      try { new Ingredient(fakeIdGenerator, "Leite", 1.0, 5.0, null); assert false; } catch (IllegalArgumentException ignored) {}
    }

    @Test
    void updateIngredient_shouldChangeFieldsCorrectly() {
        Ingredient ingredient = new Ingredient(fakeIdGenerator, "Leite", 1.0, 5.0, Unit.L);

        ingredient.setName("Leite Integral");
        ingredient.setPackageQuantity(2.0);
        ingredient.setPackagePrice(10.0);
        ingredient.setPackageUnit(Unit.L);

        assert ingredient.getName().equals("Leite Integral");
        assert ingredient.getPackageQuantity() == 2.0;
        assert ingredient.getPackagePrice() == 10.0;
        assert ingredient.getPackageUnit() == Unit.L;
        assert ingredient.getId().getValue().equals("test-id-123"); // ID não muda
    }

    @Test
    void updateIngredient_withInvalidValues_shouldThrowException() {
        Ingredient ingredient = new Ingredient(fakeIdGenerator, "Leite", 1.0, 5.0, Unit.L);

        try { ingredient.setName(""); assert false; } catch (IllegalArgumentException ignored) {}
        try { ingredient.setPackageQuantity(0); assert false; } catch (IllegalArgumentException ignored) {}
        try { ingredient.setPackagePrice(-1); assert false; } catch (IllegalArgumentException ignored) {}
    }

    @Test
    void getUnitCost_shouldReturnCorrectValue() {
        Ingredient ingredient = new Ingredient(fakeIdGenerator, "Leite", 1.0, 5.0, Unit.L);

        double expectedUnitCost = 5.0 / Unit.L.toBase(1.0);
        assert ingredient.getUnitCost() == expectedUnitCost;
    }
}
