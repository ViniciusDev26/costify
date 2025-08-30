package br.unifor.costify.application.contracts;

import br.unifor.costify.domain.entity.Ingredient;
import br.unifor.costify.domain.valueobject.Id;
import java.util.Optional;

public interface IngredientRepository {

  Ingredient save(Ingredient ingredient);

  Optional<Ingredient> findById(Id id);

  boolean existsByName(String name);

  void deleteById(Id id);
}
