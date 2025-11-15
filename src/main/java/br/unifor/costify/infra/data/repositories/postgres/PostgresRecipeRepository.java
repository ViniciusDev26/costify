package br.unifor.costify.infra.data.repositories.postgres;

import br.unifor.costify.application.contracts.RecipeRepository;
import br.unifor.costify.domain.entity.Recipe;
import br.unifor.costify.domain.valueobject.Id;
import br.unifor.costify.infra.data.entities.RecipeTable;
import br.unifor.costify.infra.data.repositories.jpa.JpaRecipeRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class PostgresRecipeRepository implements RecipeRepository {
  private final JpaRecipeRepository jpaRecipeRepository;

  public PostgresRecipeRepository(JpaRecipeRepository jpaRecipeRepository) {
    this.jpaRecipeRepository = jpaRecipeRepository;
  }

  @Override
  public Recipe save(Recipe recipe) {
    RecipeTable recipeTable = RecipeTable.fromDomain(recipe);
    RecipeTable savedRecipe = this.jpaRecipeRepository.save(recipeTable);
    return RecipeTable.toDomain(savedRecipe);
  }

  @Override
  public Optional<Recipe> findById(Id id) {
    RecipeTable recipeQueryResult =
        this.jpaRecipeRepository.findById(id.getValue()).orElse(null);

    return Optional.ofNullable(recipeQueryResult).map(RecipeTable::toDomain);
  }

  @Override
  public List<Recipe> findAll() {
    return this.jpaRecipeRepository.findAll().stream().map(RecipeTable::toDomain).toList();
  }

  @Override
  public boolean existsByName(String name) {
    return this.jpaRecipeRepository.existsByName(name);
  }

  @Override
  public void deleteById(Id id) {
    this.jpaRecipeRepository.deleteById(id.getValue());
  }

  @Override
  public List<Recipe> findByIngredientId(Id ingredientId) {
    return this.jpaRecipeRepository.findByIngredientsIngredientId(ingredientId.getValue())
        .stream()
        .map(RecipeTable::toDomain)
        .toList();
  }
}