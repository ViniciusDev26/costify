package br.unifor.costify.recipe.infra.data.repositories.jpa;

import br.unifor.costify.recipe.infra.data.entities.RecipeTable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface JpaRecipeRepository extends JpaRepository<RecipeTable, String> {
  boolean existsByName(String name);

  List<RecipeTable> findByIngredientsIngredientId(String ingredientId);
}