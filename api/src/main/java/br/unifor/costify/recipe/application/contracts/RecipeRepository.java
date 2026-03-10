package br.unifor.costify.recipe.application.contracts;

import br.unifor.costify.recipe.domain.entity.Recipe;
import br.unifor.costify.shared.domain.valueobject.Id;
import java.util.List;
import java.util.Optional;

public interface RecipeRepository {

  Recipe save(Recipe recipe);

  Optional<Recipe> findById(Id id);

  List<Recipe> findAll();

  boolean existsByName(String name);

  void deleteById(Id id);

  List<Recipe> findByIngredientId(Id ingredientId);
}
