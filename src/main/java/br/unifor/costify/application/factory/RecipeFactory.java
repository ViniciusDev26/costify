package br.unifor.costify.application.factory;

import br.unifor.costify.domain.contracts.IdGenerator;
import br.unifor.costify.domain.entity.Recipe;
import br.unifor.costify.domain.valueobject.Id;
import br.unifor.costify.domain.valueobject.Money;
import br.unifor.costify.domain.valueobject.RecipeIngredient;
import org.springframework.stereotype.Component;
import java.util.List;

@Component
public class RecipeFactory {
  private final IdGenerator idGenerator;

  public RecipeFactory(IdGenerator idGenerator) {
    this.idGenerator = idGenerator;
  }

  public Recipe create(String name, List<RecipeIngredient> ingredients, Money totalCost) {
    return new Recipe(idGenerator, name, ingredients, totalCost);
  }

  public Recipe create(Id id, String name, List<RecipeIngredient> ingredients, Money totalCost) {
    return new Recipe(id, name, ingredients, totalCost);
  }
}
