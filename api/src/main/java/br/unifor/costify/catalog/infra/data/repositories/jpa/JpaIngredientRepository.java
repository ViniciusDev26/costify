package br.unifor.costify.catalog.infra.data.repositories.jpa;

import br.unifor.costify.catalog.infra.data.entities.IngredientTable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JpaIngredientRepository extends JpaRepository<IngredientTable, String> {
  boolean existsByName(String name);
}
