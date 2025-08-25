package br.unifor.costify.application.dto;

import br.unifor.costify.application.dto.entity.IngredientDto;
import br.unifor.costify.domain.contracts.IdGenerator;
import br.unifor.costify.domain.entity.Ingredient;
import br.unifor.costify.domain.valueobject.Unit;
import org.junit.jupiter.api.Test;

class IngredientDtoTest {

  @Test
  void from_withIngredient_shouldCreateCorrectDto() {
    IdGenerator fakeIdGenerator = () -> "test-id-123";
    Ingredient ingredient = new Ingredient(fakeIdGenerator, "Flour", 1.0, 5.0, Unit.KG);

    IngredientDto dto = IngredientDto.from(ingredient);

    assert dto.id().equals("test-id-123");
    assert dto.name().equals("Flour");
    assert dto.packageQuantity() == 1.0;
    assert dto.packagePrice() == 5.0;
    assert dto.packageUnit() == Unit.KG;
    assert dto.unitCost() == ingredient.getUnitCost();
  }

  @Test
  void from_withDifferentIngredient_shouldCreateCorrectDto() {
    IdGenerator fakeIdGenerator = () -> "sugar-id-456";
    Ingredient ingredient = new Ingredient(fakeIdGenerator, "Sugar", 2.0, 8.0, Unit.KG);

    IngredientDto dto = IngredientDto.from(ingredient);

    assert dto.id().equals("sugar-id-456");
    assert dto.name().equals("Sugar");
    assert dto.packageQuantity() == 2.0;
    assert dto.packagePrice() == 8.0;
    assert dto.packageUnit() == Unit.KG;
    assert dto.unitCost() == ingredient.getUnitCost();
  }
}
