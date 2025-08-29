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

  @Test
  void save_withDuplicateName_shouldThrowDataIntegrityViolationException() {
    // Create test ingredient first
    ingredientRepository.save(new Ingredient(Id.of("ingredient-1"), "Test Ingredient", 1000.0, Money.of(5.0), Unit.G));
    
    // Given - save first recipe
    List<RecipeIngredient> ingredients = List.of(
        new RecipeIngredient(Id.of("ingredient-1"), 500.0, Unit.G)
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
    // Create test ingredients first
    ingredientRepository.save(new Ingredient(Id.of("ingredient-1"), "Test Ingredient 1", 1000.0, Money.of(5.0), Unit.G));
    ingredientRepository.save(new Ingredient(Id.of("ingredient-2"), "Test Ingredient 2", 1000.0, Money.of(6.0), Unit.G));
    ingredientRepository.save(new Ingredient(Id.of("ingredient-3"), "Test Ingredient 3", 12.0, Money.of(4.0), Unit.UN));
    
    // Given - save original recipe
    List<RecipeIngredient> originalIngredients = List.of(
        new RecipeIngredient(Id.of("ingredient-1"), 500.0, Unit.G)
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
        new RecipeIngredient(Id.of("ingredient-2"), 750.0, Unit.G),
        new RecipeIngredient(Id.of("ingredient-3"), 2.0, Unit.UN)
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
        new RecipeIngredient(Id.of("free-ingredient"), 1.0, Unit.G)
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
        new RecipeIngredient(Id.of("ingredient-1"), 1.0, Unit.G)
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
        new RecipeIngredient(Id.of("expensive-ingredient"), 999.999, Unit.G)
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
  void save_withManyIngredients_shouldPersistAll() {
    // Given - recipe with many ingredients
    List<RecipeIngredient> manyIngredients = List.of(
        new RecipeIngredient(Id.of("ingredient-1"), 100.0, Unit.G),
        new RecipeIngredient(Id.of("ingredient-2"), 200.0, Unit.ML),
        new RecipeIngredient(Id.of("ingredient-3"), 50.0, Unit.G),
        new RecipeIngredient(Id.of("ingredient-4"), 2.0, Unit.UN),
        new RecipeIngredient(Id.of("ingredient-5"), 0.5, Unit.L),
        new RecipeIngredient(Id.of("ingredient-6"), 1.5, Unit.KG),
        new RecipeIngredient(Id.of("ingredient-7"), 75.0, Unit.ML),
        new RecipeIngredient(Id.of("ingredient-8"), 3.0, Unit.UN),
        new RecipeIngredient(Id.of("ingredient-9"), 25.0, Unit.G),
        new RecipeIngredient(Id.of("ingredient-10"), 150.0, Unit.ML)
    );
    
    Recipe complexRecipe = new Recipe(
        Id.of("complex-recipe"),
        "Complex Recipe with Many Ingredients",
        manyIngredients,
        Money.of(45.50)
    );

    // When
    Recipe saved = recipeRepository.save(complexRecipe);

    // Then - all ingredients should be persisted
    assert saved.getIngredients().size() == 10;
    
    // Verify from database
    var found = recipeRepository.findById(Id.of("complex-recipe"));
    assert found.isPresent();
    assert found.get().getIngredients().size() == 10;
    
    // Verify specific ingredients are preserved
    List<RecipeIngredient> foundIngredients = found.get().getIngredients();
    assert foundIngredients.stream().anyMatch(ri -> ri.getIngredientId().getValue().equals("ingredient-1"));
    assert foundIngredients.stream().anyMatch(ri -> ri.getIngredientId().getValue().equals("ingredient-10"));
  }

  @Test
  void save_withAllUnitsInIngredients_shouldPersistCorrectly() {
    // Test recipe with all available units
    Unit[] allUnits = {Unit.G, Unit.KG, Unit.ML, Unit.L, Unit.UN};
    
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
        new RecipeIngredient(Id.of("ingredient-1"), 100.0, Unit.G),
        new RecipeIngredient(Id.of("ingredient-2"), 200.0, Unit.ML)
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
        new RecipeIngredient(Id.of("ingredient-3"), 300.0, Unit.G)
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
    assert saved.getIngredients().get(0).getIngredientId().getValue().equals("ingredient-3");
    assert saved.getIngredients().get(0).getQuantity() == 300.0;

    // Verify from database
    var found = recipeRepository.findById(Id.of("cascade-test-recipe"));
    assert found.isPresent();
    assert found.get().getIngredients().size() == 1;
    assert found.get().getIngredients().get(0).getIngredientId().getValue().equals("ingredient-3");
  }
}