package br.unifor.costify.integration.repository.recipe;

import br.unifor.costify.TestcontainersConfiguration;
import br.unifor.costify.domain.entity.Ingredient;
import br.unifor.costify.domain.entity.Recipe;
import br.unifor.costify.domain.valueobject.Id;
import br.unifor.costify.domain.valueobject.Money;
import br.unifor.costify.domain.valueobject.RecipeIngredient;
import br.unifor.costify.domain.valueobject.Unit;
import br.unifor.costify.infra.data.repositories.jpa.JpaIngredientRepository;
import br.unifor.costify.infra.data.repositories.jpa.JpaRecipeRepository;
import br.unifor.costify.infra.data.repositories.postgres.PostgresIngredientRepository;
import br.unifor.costify.infra.data.repositories.postgres.PostgresRecipeRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.dao.DataIntegrityViolationException;

import java.util.List;

@SpringBootTest
@Import(TestcontainersConfiguration.class)
class RecipeRepositoryConstraintsIntegrationTest {

  @Autowired private PostgresRecipeRepository recipeRepository;
  @Autowired private PostgresIngredientRepository ingredientRepository;

  @Autowired private JpaRecipeRepository jpaRecipeRepository;
  @Autowired private JpaIngredientRepository jpaIngredientRepository;

  @AfterEach
  void cleanup() {
    // Clean up after each test to ensure proper isolation
    jpaRecipeRepository.deleteAll();
    jpaIngredientRepository.deleteAll();
  }

  @BeforeEach
  void setUp() {
    // Create base test ingredients that will be reused across tests
    createBaseTestIngredients();
  }
  
  private void createBaseTestIngredients() {
    // Create standard ingredients that can be reused across tests
    ingredientRepository.save(new Ingredient(Id.of("base-flour"), "Base Flour", 1000.0, Money.of(3.50), Unit.G));
    ingredientRepository.save(new Ingredient(Id.of("base-sugar"), "Base Sugar", 1000.0, Money.of(4.00), Unit.G));
    ingredientRepository.save(new Ingredient(Id.of("base-eggs"), "Base Eggs", 12.0, Money.of(5.00), Unit.UN));
  }

  @Test
  void save_withDuplicateName_shouldThrowDataIntegrityViolationException() {
    // Given - save first recipe
    List<RecipeIngredient> ingredients = List.of(
        new RecipeIngredient(Id.of("base-flour"), 500.0, Unit.G)
    );
    
    Recipe firstRecipe = new Recipe(
        Id.of("first-id"),
        "Duplicate Recipe Name",
        ingredients,
        Money.of(10.0)
    );
    recipeRepository.save(firstRecipe);

    // When/Then - try to save second recipe with same name
    Recipe secondRecipe = new Recipe(
        Id.of("second-id"),
        "Duplicate Recipe Name", // Same name
        ingredients,
        Money.of(15.0)
    );

    try {
      recipeRepository.save(secondRecipe);
      assert false : "Should have thrown DataIntegrityViolationException";
    } catch (DataIntegrityViolationException expected) {
      // Expected due to unique constraint on name
    }
  }

  @Test
  void save_withDuplicateId_shouldUpdateExistingRecord() {
    // Given - save original recipe
    List<RecipeIngredient> originalIngredients = List.of(
        new RecipeIngredient(Id.of("base-flour"), 500.0, Unit.G)
    );
    
    Recipe original = new Recipe(
        Id.of("duplicate-id"),
        "Original Recipe",
        originalIngredients,
        Money.of(10.0)
    );
    recipeRepository.save(original);

    // When - save new recipe with same ID (immutable update pattern)
    List<RecipeIngredient> updatedIngredients = List.of(
        new RecipeIngredient(Id.of("base-sugar"), 750.0, Unit.G),
        new RecipeIngredient(Id.of("base-eggs"), 2.0, Unit.UN)
    );
    
    Recipe updated = new Recipe(
        Id.of("duplicate-id"), // Same ID
        "Updated Recipe", // Different name
        updatedIngredients, // Different ingredients
        Money.of(20.0) // Different cost
    );
    Recipe saved = recipeRepository.save(updated);

    // Then - should update the existing record
    assert saved.getName().equals("Updated Recipe");
    assert saved.getTotalCost().doubleValue() == 20.0;
    assert saved.getIngredients().size() == 2;

    // Verify only one record exists
    var found = recipeRepository.findById(Id.of("duplicate-id"));
    assert found.isPresent();
    assert found.get().getName().equals("Updated Recipe");
    assert found.get().getIngredients().size() == 2;
  }

  @Test
  void save_withZeroCost_shouldBeAllowed() {
    // Given - recipe with zero cost
    List<RecipeIngredient> freeIngredients = List.of(
        new RecipeIngredient(Id.of("base-flour"), 1.0, Unit.G)
    );
    
    Recipe freeRecipe = new Recipe(
        Id.of("free-recipe"),
        "Free Sample Recipe",
        freeIngredients,
        Money.of(0.0) // Zero cost should be allowed
    );

    // When
    Recipe saved = recipeRepository.save(freeRecipe);

    // Then
    assert saved.getTotalCost().doubleValue() == 0.0;
  }

  @Test
  void save_withLongName_shouldThrowDatabaseConstraintViolation() {
    // Given - very long name (database column is VARCHAR(255))
    String longName = "A".repeat(300); // 300 characters
    List<RecipeIngredient> ingredients = List.of(
        new RecipeIngredient(Id.of("base-flour"), 1.0, Unit.G)
    );
    
    Recipe longNameRecipe = new Recipe(
        Id.of("long-name-id"),
        longName,
        ingredients,
        Money.of(5.0)
    );

    try {
      // When
      recipeRepository.save(longNameRecipe);
      assert false : "Should have thrown database constraint violation for name length";
    } catch (Exception e) {
      // Then - should throw database constraint violation
      assert e instanceof DataIntegrityViolationException
          || e.getMessage().contains("value too long")
          || e.getMessage().contains("too long");
    }
  }

  @Test
  void save_withVeryLargeCost_shouldHandlePrecision() {
    // Given - recipe with large precise cost
    List<RecipeIngredient> ingredients = List.of(
        new RecipeIngredient(Id.of("base-sugar"), 999.999, Unit.G)
    );
    
    Recipe expensiveRecipe = new Recipe(
        Id.of("expensive-recipe"),
        "Expensive Recipe",
        ingredients,
        Money.of(99999.99) // Large cost with 2 decimal places
    );

    // When
    Recipe saved = recipeRepository.save(expensiveRecipe);

    // Then - precision should be maintained
    assert saved.getTotalCost().doubleValue() == 99999.99;

    // Verify after database round-trip
    var found = recipeRepository.findById(Id.of("expensive-recipe"));
    assert found.isPresent();
    assert found.get().getTotalCost().doubleValue() == 99999.99;
  }

  @Test
  void save_withAllUnitsInIngredients_shouldPersistCorrectly() {
    // Create ingredients with different units  
    ingredientRepository.save(new Ingredient(Id.of("gram-ingredient"), "Flour Special", 1000.0, Money.of(3.0), Unit.G));
    ingredientRepository.save(new Ingredient(Id.of("kg-ingredient"), "Sugar Bulk", 1.0, Money.of(4.0), Unit.KG));
    ingredientRepository.save(new Ingredient(Id.of("ml-ingredient"), "Vanilla Extract", 100.0, Money.of(8.0), Unit.ML));
    ingredientRepository.save(new Ingredient(Id.of("liter-ingredient"), "Milk Premium", 1.0, Money.of(2.5), Unit.L));
    ingredientRepository.save(new Ingredient(Id.of("unit-ingredient"), "Eggs Organic", 12.0, Money.of(6.0), Unit.UN));
    
    // Test recipe with all available units
    List<RecipeIngredient> allUnitsIngredients = List.of(
        new RecipeIngredient(Id.of("gram-ingredient"), 250.0, Unit.G),
        new RecipeIngredient(Id.of("kg-ingredient"), 1.5, Unit.KG),
        new RecipeIngredient(Id.of("ml-ingredient"), 500.0, Unit.ML),
        new RecipeIngredient(Id.of("liter-ingredient"), 0.3, Unit.L),
        new RecipeIngredient(Id.of("unit-ingredient"), 5.0, Unit.UN)
    );

    Recipe allUnitsRecipe = new Recipe(
        Id.of("all-units-recipe"),
        "Recipe with All Units",
        allUnitsIngredients,
        Money.of(25.75)
    );

    // When
    Recipe saved = recipeRepository.save(allUnitsRecipe);

    // Then - verify all units are preserved
    List<RecipeIngredient> savedIngredients = saved.getIngredients();
    assert savedIngredients.stream().anyMatch(ri -> ri.getUnit() == Unit.G);
    assert savedIngredients.stream().anyMatch(ri -> ri.getUnit() == Unit.KG);
    assert savedIngredients.stream().anyMatch(ri -> ri.getUnit() == Unit.ML);
    assert savedIngredients.stream().anyMatch(ri -> ri.getUnit() == Unit.L);
    assert savedIngredients.stream().anyMatch(ri -> ri.getUnit() == Unit.UN);

    // Verify from database
    var found = recipeRepository.findById(saved.getId());
    assert found.isPresent();
    List<RecipeIngredient> foundIngredients = found.get().getIngredients();
    assert foundIngredients.stream().anyMatch(ri -> ri.getUnit() == Unit.G);
    assert foundIngredients.stream().anyMatch(ri -> ri.getUnit() == Unit.KG);
    assert foundIngredients.stream().anyMatch(ri -> ri.getUnit() == Unit.ML);
    assert foundIngredients.stream().anyMatch(ri -> ri.getUnit() == Unit.L);
    assert foundIngredients.stream().anyMatch(ri -> ri.getUnit() == Unit.UN);
  }

  @Test
  void findById_withNullId_shouldHandleGracefully() {
    try {
      // When
      recipeRepository.findById(null);
      assert false : "Should handle null ID gracefully";
    } catch (Exception e) {
      // Then - should throw appropriate exception
      assert e instanceof IllegalArgumentException || e instanceof NullPointerException;
    }
  }

  @Test
  void existsByName_withNullName_shouldReturnFalse() {
    // When - Spring Data JPA handles null gracefully and generates "WHERE name IS NULL"
    boolean exists = recipeRepository.existsByName(null);

    // Then - should return false since no recipe has null name
    assert !exists;
  }

  @Test
  void deleteById_withNonExistingId_shouldNotThrowException() {
    // Given - non-existing ID
    Id nonExistingId = Id.of("definitely-does-not-exist");

    // When/Then - should not throw exception
    recipeRepository.deleteById(nonExistingId);

    // Verify it's still not there
    var found = recipeRepository.findById(nonExistingId);
    assert found.isEmpty();
  }

  @Test
  void save_shouldHandleCascadeDeleteOfIngredients() {
    // Given - save a recipe with ingredients
    List<RecipeIngredient> ingredients = List.of(
        new RecipeIngredient(Id.of("base-flour"), 100.0, Unit.G),
        new RecipeIngredient(Id.of("base-sugar"), 200.0, Unit.G)
    );
    
    Recipe originalRecipe = new Recipe(
        Id.of("cascade-test-recipe"),
        "Cascade Test Recipe",
        ingredients,
        Money.of(10.0)
    );
    recipeRepository.save(originalRecipe);

    // When - update recipe with different ingredients (should cascade delete old ones)
    List<RecipeIngredient> newIngredients = List.of(
        new RecipeIngredient(Id.of("base-eggs"), 3.0, Unit.UN)
    );
    
    Recipe updatedRecipe = new Recipe(
        Id.of("cascade-test-recipe"), // Same ID
        "Updated Cascade Test Recipe",
        newIngredients, // Completely different ingredients
        Money.of(15.0)
    );
    Recipe saved = recipeRepository.save(updatedRecipe);

    // Then - old ingredients should be removed, new ones added
    assert saved.getIngredients().size() == 1;
    assert saved.getIngredients().get(0).getIngredientId().getValue().equals("base-eggs");
    assert saved.getIngredients().get(0).getQuantity() == 3.0;

    // Verify from database
    var found = recipeRepository.findById(Id.of("cascade-test-recipe"));
    assert found.isPresent();
    assert found.get().getIngredients().size() == 1;
    assert found.get().getIngredients().get(0).getIngredientId().getValue().equals("base-eggs");
  }
}