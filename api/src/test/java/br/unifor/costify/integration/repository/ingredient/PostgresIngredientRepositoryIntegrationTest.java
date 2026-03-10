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
  void save_shouldHandleTbspUnits() {
    // Test with TBSP (liquid volume) units
    Ingredient tbspLiquidIngredient =
        new Ingredient(Id.of("tbsp-liquid"), "Vanilla Extract", 10.0, Money.of(8.50), Unit.TBSP);

    // Test with TBSP_BUTTER (solid fat weight) units  
    Ingredient tbspButterIngredient =
        new Ingredient(Id.of("tbsp-butter"), "Butter", 32.0, Money.of(12.00), Unit.TBSP_BUTTER);

    // Save both
    Ingredient savedTbspLiquid = ingredientRepository.save(tbspLiquidIngredient);
    Ingredient savedTbspButter = ingredientRepository.save(tbspButterIngredient);

    // Verify TBSP units are preserved
    assert savedTbspLiquid.getPackageUnit() == Unit.TBSP;
    assert savedTbspButter.getPackageUnit() == Unit.TBSP_BUTTER;

    // Verify they can be retrieved from database
    Optional<Ingredient> foundTbspLiquid = ingredientRepository.findById(tbspLiquidIngredient.getId());
    Optional<Ingredient> foundTbspButter = ingredientRepository.findById(tbspButterIngredient.getId());

    assert foundTbspLiquid.isPresent();
    assert foundTbspButter.isPresent();
    assert foundTbspLiquid.get().getPackageUnit() == Unit.TBSP;
    assert foundTbspButter.get().getPackageUnit() == Unit.TBSP_BUTTER;
  }

  @Test
  void save_shouldCalculateUnitCostCorrectlyForTbspUnits() {
    // Test TBSP (liquid) cost calculation: 1 TBSP = 15ml
    Ingredient tbspLiquid =
        new Ingredient(
            Id.of("tbsp-cost-test"),
            "Vanilla Extract",
            10.0, // 10 tablespoons
            Money.of(8.50), // $8.50 total
            Unit.TBSP
            );

    // Test TBSP_BUTTER (solid) cost calculation: 1 TBSP = 14g
    Ingredient tbspButter =
        new Ingredient(
            Id.of("tbsp-butter-cost-test"),
            "Butter", 
            16.0, // 16 tablespoons
            Money.of(12.00), // $12.00 total
            Unit.TBSP_BUTTER
            );

    // Save both
    Ingredient savedTbspLiquid = ingredientRepository.save(tbspLiquid);
    Ingredient savedTbspButter = ingredientRepository.save(tbspButter);

    // Verify TBSP liquid cost: $8.50 / (10 * 15ml) = $8.50 / 150ml = $0.0567 per ml
    double expectedTbspLiquidCost = 8.50 / Unit.TBSP.toBase(10.0);
    assert Math.abs(savedTbspLiquid.getUnitCost() - expectedTbspLiquidCost) < 0.0001;

    // Verify TBSP butter cost: $12.00 / (16 * 14g) = $12.00 / 224g = $0.0536 per g
    double expectedTbspButterCost = 12.00 / Unit.TBSP_BUTTER.toBase(16.0);
    assert Math.abs(savedTbspButter.getUnitCost() - expectedTbspButterCost) < 0.0001;

    // Verify after retrieval from database
    Optional<Ingredient> foundTbspLiquid = ingredientRepository.findById(tbspLiquid.getId());
    Optional<Ingredient> foundTbspButter = ingredientRepository.findById(tbspButter.getId());

    assert foundTbspLiquid.isPresent();
    assert foundTbspButter.isPresent();
    assert Math.abs(foundTbspLiquid.get().getUnitCost() - expectedTbspLiquidCost) < 0.0001;
    assert Math.abs(foundTbspButter.get().getUnitCost() - expectedTbspButterCost) < 0.0001;
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

  @Test
  void findAll_shouldReturnEmptyList_whenNoIngredients() {
    // When
    var ingredients = ingredientRepository.findAll();

    // Then
    assert ingredients != null;
    assert ingredients.isEmpty();
  }

  @Test
  void findAll_shouldReturnAllIngredients() {
    // Given
    Ingredient ingredient1 =
        new Ingredient(Id.of("ingredient-1"), "Milk", 1.0, Money.of(5.50), Unit.L);
    Ingredient ingredient2 = new Ingredient(Id.of("ingredient-2"), "Flour", 500.0, Money.of(3.20), Unit.G);
    Ingredient ingredient3 =
        new Ingredient(Id.of("ingredient-3"), "Sugar", 1.0, Money.of(4.50), Unit.KG);

    ingredientRepository.save(ingredient1);
    ingredientRepository.save(ingredient2);
    ingredientRepository.save(ingredient3);

    // When
    var ingredients = ingredientRepository.findAll();

    // Then
    assert ingredients != null;
    assert ingredients.size() == 3;
  }

  @Test
  void findAll_shouldReturnIngredientsWithCorrectData() {
    // Given
    Ingredient milk = new Ingredient(Id.of("milk-id"), "Milk", 1.0, Money.of(5.50), Unit.L);
    Ingredient flour = new Ingredient(Id.of("flour-id"), "Flour", 500.0, Money.of(3.20), Unit.G);

    ingredientRepository.save(milk);
    ingredientRepository.save(flour);

    // When
    var ingredients = ingredientRepository.findAll();

    // Then
    assert ingredients.size() == 2;

    // Find milk in results
    var foundMilk =
        ingredients.stream().filter(i -> i.getId().equals(milk.getId())).findFirst();
    assert foundMilk.isPresent();
    assert foundMilk.get().getName().equals("Milk");
    assert foundMilk.get().getPackageQuantity() == 1.0;
    assert foundMilk.get().getPackagePrice().equals(Money.of(5.50));
    assert foundMilk.get().getPackageUnit() == Unit.L;

    // Find flour in results
    var foundFlour =
        ingredients.stream().filter(i -> i.getId().equals(flour.getId())).findFirst();
    assert foundFlour.isPresent();
    assert foundFlour.get().getName().equals("Flour");
    assert foundFlour.get().getPackageQuantity() == 500.0;
    assert foundFlour.get().getPackagePrice().equals(Money.of(3.20));
    assert foundFlour.get().getPackageUnit() == Unit.G;
  }
}
