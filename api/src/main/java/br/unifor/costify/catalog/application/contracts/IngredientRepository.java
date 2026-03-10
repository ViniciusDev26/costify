package br.unifor.costify.catalog.application.contracts;

import br.unifor.costify.catalog.domain.entity.Ingredient;
import br.unifor.costify.shared.domain.valueobject.Id;
import java.util.List;
import java.util.Optional;

public interface IngredientRepository {

  Ingredient save(Ingredient ingredient);

  Optional<Ingredient> findById(Id id);

  List<Ingredient> findAll();

  boolean existsByName(String name);

  void deleteById(Id id);
}
