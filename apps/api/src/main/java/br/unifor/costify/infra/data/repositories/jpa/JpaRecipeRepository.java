package br.unifor.costify.infra.data.repositories.jpa;

import br.unifor.costify.infra.data.entities.RecipeTable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JpaRecipeRepository extends JpaRepository<RecipeTable, String> {
  boolean existsByName(String name);
}