package br.unifor.costify.infra.data.repositories.jpa;

import br.unifor.costify.infra.data.entities.IngredientTable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JpaIngredientRepository extends JpaRepository<IngredientTable, String> {
  boolean existsByName(String name);
}
