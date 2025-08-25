package br.unifor.costify.infra.data.repositories.postgres;

import br.unifor.costify.application.contracts.IngredientRepository;
import br.unifor.costify.domain.entity.Ingredient;
import br.unifor.costify.domain.valueobject.Id;
import br.unifor.costify.infra.data.entities.IngredientTable;
import br.unifor.costify.infra.data.repositories.jpa.JpaIngredientRepository;
import java.util.Optional;
import org.springframework.stereotype.Repository;

@Repository
public class PostgresIngredientRepository implements IngredientRepository {
  private final JpaIngredientRepository jpaIngredientRepository;

  public PostgresIngredientRepository(JpaIngredientRepository jpaIngredientRepository) {
    this.jpaIngredientRepository = jpaIngredientRepository;
  }

  @Override
  public final Optional<Ingredient> findById(Id id) {
    IngredientTable ingredientQueryResult =
        this.jpaIngredientRepository.findById(id.getValue()).orElse(null);

    return Optional.ofNullable(ingredientQueryResult).map(IngredientTable::toDomain);
  }

  public Ingredient save(Ingredient ingredient) {
    IngredientTable ingredientTable = IngredientTable.fromDomain(ingredient);
    IngredientTable savedIngredient = this.jpaIngredientRepository.save(ingredientTable);
    return IngredientTable.toDomain(savedIngredient);
  }

  public void deleteById(Id id) {
    this.jpaIngredientRepository.deleteById(id.getValue());
  }

  public boolean existsByName(String name) {
    return this.jpaIngredientRepository.existsByName(name);
  }
}
