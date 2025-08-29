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
import java.util.Optional;

@SpringBootTest
@Import(TestcontainersConfiguration.class)
class BasicRecipeRepositoryIntegrationTest {

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
    // Create base test ingredients for all tests
    createTestIngredients();
  }
  
  private void createTestIngredients() {
    ingredientRepository.save(new Ingredient(Id.of("flour-ingredient"), "Flour", 1000.0, Money.of(3.50), Unit.G));
    ingredientRepository.save(new Ingredient(Id.of("egg-ingredient"), "Eggs", 12.0, Money.of(4.80), Unit.UN));
    ingredientRepository.save(new Ingredient(Id.of("milk-ingredient"), "Milk", 1000.0, Money.of(2.50), Unit.ML));
  }

  @Test
  void save_shouldPersistBasicRecipeToDatabase() {
    // Given
    List<RecipeIngredient> ingredients = List.of(
        new RecipeIngredient(Id.of("flour-ingredient"), 500.0, Unit.G),
        new RecipeIngredient(Id.of("egg-ingredient"), 2.0, Unit.UN)
    );
    
    Recipe testRecipe = new Recipe(
        Id.of("basic-recipe-id"),
        "Basic Cake",
        ingredients,
        Money.of(15.50)
    );

    // When
    Recipe saved = recipeRepository.save(testRecipe);

    // Then
    assert saved != null;
    assert saved.getId().equals(testRecipe.getId());
    assert saved.getName().equals(testRecipe.getName());
    assert saved.getTotalCost().equals(testRecipe.getTotalCost());
    assert saved.getIngredients().size() == 2;
  }

  @Test
  void findById_withExistingId_shouldReturnRecipe() {
    // Given
    List<RecipeIngredient> ingredients = List.of(
        new RecipeIngredient(Id.of("flour-ingredient"), 300.0, Unit.G)
    );
    
    Recipe testRecipe = new Recipe(
        Id.of("findable-recipe"),
        "Findable Recipe",
        ingredients,
        Money.of(8.00)
    );
    recipeRepository.save(testRecipe);

    // When
    Optional<Recipe> found = recipeRepository.findById(testRecipe.getId());

    // Then
    assert found.isPresent();
    Recipe recipe = found.get();
    assert recipe.getId().equals(testRecipe.getId());
    assert recipe.getName().equals(testRecipe.getName());
    assert recipe.getTotalCost().equals(testRecipe.getTotalCost());
    assert recipe.getIngredients().size() == 1;
  }

  @Test
  void findById_withNonExistingId_shouldReturnEmpty() {
    // Given
    Id nonExistingId = Id.of("non-existing-recipe");

    // When
    Optional<Recipe> found = recipeRepository.findById(nonExistingId);

    // Then
    assert found.isEmpty();
  }

  @Test
  void existsByName_withExistingName_shouldReturnTrue() {
    // Given
    List<RecipeIngredient> ingredients = List.of(
        new RecipeIngredient(Id.of("flour-ingredient"), 200.0, Unit.G)
    );
    
    Recipe testRecipe = new Recipe(
        Id.of("exists-recipe"),
        "Existing Recipe",
        ingredients,
        Money.of(6.00)
    );
    recipeRepository.save(testRecipe);

    // When
    boolean exists = recipeRepository.existsByName("Existing Recipe");

    // Then
    assert exists;
  }

  @Test
  void existsByName_withNonExistingName_shouldReturnFalse() {
    // When
    boolean exists = recipeRepository.existsByName("Non-existing recipe");

    // Then
    assert !exists;
  }

  @Test
  void deleteById_shouldRemoveRecipeFromDatabase() {
    // Given
    List<RecipeIngredient> ingredients = List.of(
        new RecipeIngredient(Id.of("flour-ingredient"), 400.0, Unit.G)
    );
    
    Recipe testRecipe = new Recipe(
        Id.of("deletable-recipe"),
        "Deletable Recipe",
        ingredients,
        Money.of(10.00)
    );
    recipeRepository.save(testRecipe);
    assert recipeRepository.findById(testRecipe.getId()).isPresent();

    // When
    recipeRepository.deleteById(testRecipe.getId());

    // Then
    Optional<Recipe> found = recipeRepository.findById(testRecipe.getId());
    assert found.isEmpty();
  }

  @Test
  void save_withDuplicateName_shouldThrowDataIntegrityViolationException() {
    // Given - save first recipe
    List<RecipeIngredient> ingredients = List.of(
        new RecipeIngredient(Id.of("flour-ingredient"), 500.0, Unit.G)
    );
    
    Recipe firstRecipe = new Recipe(
        Id.of("first-recipe"),
        "Duplicate Name Recipe",
        ingredients,
        Money.of(10.0)
    );
    recipeRepository.save(firstRecipe);

    // When/Then - try to save second recipe with same name
    Recipe secondRecipe = new Recipe(
        Id.of("second-recipe"),
        "Duplicate Name Recipe", // Same name
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
  void save_shouldHandleZeroCostRecipe() {
    // Given - recipe with zero cost
    List<RecipeIngredient> ingredients = List.of(
        new RecipeIngredient(Id.of("flour-ingredient"), 100.0, Unit.G)
    );
    
    Recipe freeRecipe = new Recipe(
        Id.of("free-recipe"),
        "Free Sample Recipe",
        ingredients,
        Money.of(0.0) // Zero cost should be allowed
    );

    // When
    Recipe saved = recipeRepository.save(freeRecipe);

    // Then
    assert saved.getTotalCost().doubleValue() == 0.0;

    // Verify after retrieval from database
    Optional<Recipe> found = recipeRepository.findById(freeRecipe.getId());
    assert found.isPresent();
    assert found.get().getTotalCost().doubleValue() == 0.0;
  }
}