package br.unifor.costify.application.contracts;

import br.unifor.costify.domain.entity.Recipe;
import br.unifor.costify.domain.valueobject.Id;
import java.util.Optional;

public interface RecipeRepository {

  Recipe save(Recipe recipe);

  Optional<Recipe> findById(Id id);

  boolean existsByName(String name);

  void deleteById(Id id);
}
