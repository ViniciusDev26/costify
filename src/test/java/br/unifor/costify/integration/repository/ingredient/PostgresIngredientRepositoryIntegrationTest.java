package br.unifor.costify.integration.repository.ingredient;

import br.unifor.costify.TestcontainersConfiguration;
import br.unifor.costify.domain.entity.Ingredient;
import br.unifor.costify.domain.valueobject.Id;
import br.unifor.costify.domain.valueobject.Money;
import br.unifor.costify.domain.valueobject.Unit;
import br.unifor.costify.infra.data.repositories.jpa.JpaIngredientRepository;
import br.unifor.costify.infra.data.repositories.postgres.PostgresIngredientRepository;
import java.util.Optional;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

@SpringBootTest
@Import(TestcontainersConfiguration.class)
class PostgresIngredientRepositoryIntegrationTest {

  @Autowired private PostgresIngredientRepository ingredientRepository;

  @Autowired private JpaIngredientRepository jpaIngredientRepository;

  private Ingredient testIngredient;

  @AfterEach
  void cleanup() {
    // Clean up after each test to ensure proper isolation
    jpaIngredientRepository.deleteAll();
  }

  @BeforeEach
  void setUp() {
    testIngredient = new Ingredient(Id.of("test-ingredient-id"), "Test Milk", 1.0, Money.of(5.50), Unit.L);
  }

  @Test
  void save_shouldPersistIngredientToDatabase() {
    // When
    Ingredient saved = ingredientRepository.save(testIngredient);

    // Then
    assert saved != null;
    assert saved.getId().equals(testIngredient.getId());
    assert saved.getName().equals(testIngredient.getName());
    assert saved.getPackageQuantity() == testIngredient.getPackageQuantity();
    assert saved.getPackagePrice().equals(testIngredient.getPackagePrice());
    assert saved.getPackageUnit() == testIngredient.getPackageUnit();
  }

  @Test
  void findById_withExistingId_shouldReturnIngredient() {
    // Given
    ingredientRepository.save(testIngredient);

    // When
    Optional<Ingredient> found = ingredientRepository.findById(testIngredient.getId());

    // Then
    assert found.isPresent();
    Ingredient ingredient = found.get();
    assert ingredient.getId().equals(testIngredient.getId());
    assert ingredient.getName().equals(testIngredient.getName());
    assert ingredient.getPackageQuantity() == testIngredient.getPackageQuantity();
    assert ingredient.getPackagePrice().equals(testIngredient.getPackagePrice());
    assert ingredient.getPackageUnit() == testIngredient.getPackageUnit();
  }

  @Test
  void findById_withNonExistingId_shouldReturnEmpty() {
    // Given
    Id nonExistingId = Id.of("non-existing-id");

    // When
    Optional<Ingredient> found = ingredientRepository.findById(nonExistingId);

    // Then
    assert found.isEmpty();
  }

  @Test
  void existsByName_withExistingName_shouldReturnTrue() {
    // Given
    ingredientRepository.save(testIngredient);

    // When
    boolean exists = ingredientRepository.existsByName(testIngredient.getName());

    // Then
    assert exists;
  }

  @Test
  void existsByName_withNonExistingName_shouldReturnFalse() {
    // When
    boolean exists = ingredientRepository.existsByName("Non-existing ingredient");

    // Then
    assert !exists;
  }

  @Test
  void save_shouldUpdateExistingIngredient_whenImmutableEntityIsReplaced() {
    // Given - save initial ingredient
    ingredientRepository.save(testIngredient);

    // When - create new immutable entity with same ID but different values
    Ingredient updatedIngredient =
        new Ingredient(
            testIngredient.getId(), // Same ID
            "Updated Premium Milk", // Updated name
            2.0, // Updated quantity
            Money.of(12.0), // Updated price
            Unit.L // Same unit
            );
    Ingredient saved = ingredientRepository.save(updatedIngredient);

    // Then - verify the entity was replaced
    assert saved.getId().equals(testIngredient.getId());
    assert saved.getName().equals("Updated Premium Milk");
    assert saved.getPackageQuantity() == 2.0;
    assert saved.getPackagePrice().doubleValue() == 12.0;
    assert saved.getPackageUnit() == Unit.L;

    // Verify in database
    Optional<Ingredient> found = ingredientRepository.findById(testIngredient.getId());
    assert found.isPresent();
    Ingredient foundIngredient = found.get();
    assert foundIngredient.getName().equals("Updated Premium Milk");
    assert foundIngredient.getPackageQuantity() == 2.0;
  }

  @Test
  void deleteById_shouldRemoveIngredientFromDatabase() {
    // Given
    ingredientRepository.save(testIngredient);
    assert ingredientRepository.findById(testIngredient.getId()).isPresent();

    // When
    ingredientRepository.deleteById(testIngredient.getId());

    // Then
    Optional<Ingredient> found = ingredientRepository.findById(testIngredient.getId());
    assert found.isEmpty();
  }

  @Test
  void save_shouldHandleDifferentUnits() {
    // Test with different units
    Ingredient gramIngredient =
        new Ingredient(Id.of("gram-ingredient"), "Flour", 500.0, Money.of(3.20), Unit.G);

    Ingredient kgIngredient = new Ingredient(Id.of("kg-ingredient"), "Sugar", 1.0, Money.of(4.50), Unit.KG);

    Ingredient mlIngredient =
        new Ingredient(Id.of("ml-ingredient"), "Vanilla Extract", 50.0, Money.of(8.90), Unit.ML);

    // Save all
    Ingredient savedGram = ingredientRepository.save(gramIngredient);
    Ingredient savedKg = ingredientRepository.save(kgIngredient);
    Ingredient savedMl = ingredientRepository.save(mlIngredient);

    // Verify units are preserved
    assert savedGram.getPackageUnit() == Unit.G;
    assert savedKg.getPackageUnit() == Unit.KG;
    assert savedMl.getPackageUnit() == Unit.ML;
  }

  @Test
  void save_shouldCalculateUnitCostDynamically() {
    // Given
    Ingredient ingredient =
        new Ingredient(
            Id.of("cost-test"),
            "Test Product",
            2.0, // 2 units
            Money.of(10.0), // $10 total
            Unit.KG // per kg
            );

    // When
    Ingredient saved = ingredientRepository.save(ingredient);

    // Then - unit cost should be calculated dynamically
    double expectedUnitCost = 10.0 / Unit.KG.toBase(2.0); // $10 / (2 * 1000g) = $0.005 per gram
    assert saved.getUnitCost() == expectedUnitCost;

    // Verify after retrieval from database
    Optional<Ingredient> found = ingredientRepository.findById(ingredient.getId());
    assert found.isPresent();
    assert found.get().getUnitCost() == expectedUnitCost;
  }

  @Test
  void existsByName_shouldBeCaseExactMatch() {
    // Given
    ingredientRepository.save(testIngredient); // "Test Milk"

    // When/Then - exact match
    assert ingredientRepository.existsByName("Test Milk");

    // When/Then - case mismatch should not match (depends on DB collation)
    assert !ingredientRepository.existsByName("test milk");
    assert !ingredientRepository.existsByName("TEST MILK");
  }
}
