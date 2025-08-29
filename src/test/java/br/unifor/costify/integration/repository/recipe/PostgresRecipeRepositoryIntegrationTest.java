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

import java.util.List;
import java.util.Optional;

@SpringBootTest
@Import(TestcontainersConfiguration.class)
class PostgresRecipeRepositoryIntegrationTest {

  @Autowired private PostgresRecipeRepository recipeRepository;
  @Autowired private PostgresIngredientRepository ingredientRepository;

  @Autowired private JpaRecipeRepository jpaRecipeRepository;
  @Autowired private JpaIngredientRepository jpaIngredientRepository;

  private Recipe testRecipe;

  @AfterEach
  void cleanup() {
    // Clean up after each test to ensure proper isolation
    jpaRecipeRepository.deleteAll();
    jpaIngredientRepository.deleteAll();
  }

  @BeforeEach
  void setUp() {
    // Create test ingredients first to satisfy foreign key constraints
    Ingredient ingredient1 = new Ingredient(Id.of("ingredient-1"), "Flour", 1000.0, Money.of(3.50), Unit.G);
    Ingredient ingredient2 = new Ingredient(Id.of("ingredient-2"), "Eggs", 12.0, Money.of(4.80), Unit.UN);
    ingredientRepository.save(ingredient1);
    ingredientRepository.save(ingredient2);
    
    List<RecipeIngredient> ingredients = List.of(
        new RecipeIngredient(Id.of("ingredient-1"), 500.0, Unit.G),
        new RecipeIngredient(Id.of("ingredient-2"), 2.0, Unit.UN)
    );
    
    testRecipe = new Recipe(
        Id.of("test-recipe-id"),
        "Chocolate Cake",
        ingredients,
        Money.of(15.50)
    );
  }

  @Test
  void save_shouldPersistRecipeToDatabase() {
    // When
    Recipe saved = recipeRepository.save(testRecipe);

    // Then
    assert saved != null;
    assert saved.getId().equals(testRecipe.getId());
    assert saved.getName().equals(testRecipe.getName());
    assert saved.getTotalCost().equals(testRecipe.getTotalCost());
    assert saved.getIngredients().size() == testRecipe.getIngredients().size();
    
    // Verify ingredients are saved correctly
    List<RecipeIngredient> savedIngredients = saved.getIngredients();
    assert savedIngredients.get(0).getIngredientId().getValue().equals("ingredient-1");
    assert savedIngredients.get(0).getQuantity() == 500.0;
    assert savedIngredients.get(0).getUnit() == Unit.G;
    assert savedIngredients.get(1).getIngredientId().getValue().equals("ingredient-2");
    assert savedIngredients.get(1).getQuantity() == 2.0;
    assert savedIngredients.get(1).getUnit() == Unit.UN;
  }

  @Test
  void findById_withExistingId_shouldReturnRecipe() {
    // Given
    recipeRepository.save(testRecipe);

    // When
    Optional<Recipe> found = recipeRepository.findById(testRecipe.getId());

    // Then
    assert found.isPresent();
    Recipe recipe = found.get();
    assert recipe.getId().equals(testRecipe.getId());
    assert recipe.getName().equals(testRecipe.getName());
    assert recipe.getTotalCost().equals(testRecipe.getTotalCost());
    assert recipe.getIngredients().size() == testRecipe.getIngredients().size();
  }

  @Test
  void findById_withNonExistingId_shouldReturnEmpty() {
    // Given
    Id nonExistingId = Id.of("non-existing-id");

    // When
    Optional<Recipe> found = recipeRepository.findById(nonExistingId);

    // Then
    assert found.isEmpty();
  }

  @Test
  void existsByName_withExistingName_shouldReturnTrue() {
    // Given
    recipeRepository.save(testRecipe);

    // When
    boolean exists = recipeRepository.existsByName(testRecipe.getName());

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
  void save_shouldUpdateExistingRecipe_whenImmutableEntityIsReplaced() {
    // Given - save initial recipe
    recipeRepository.save(testRecipe);

    // When - create new immutable entity with same ID but different values
    List<RecipeIngredient> updatedIngredients = List.of(
        new RecipeIngredient(Id.of("ingredient-3"), 300.0, Unit.G),
        new RecipeIngredient(Id.of("ingredient-4"), 1.5, Unit.L)
    );
    
    Recipe updatedRecipe = new Recipe(
        testRecipe.getId(), // Same ID
        "Updated Premium Chocolate Cake", // Updated name
        updatedIngredients, // Updated ingredients
        Money.of(25.75) // Updated cost
    );
    Recipe saved = recipeRepository.save(updatedRecipe);

    // Then - verify the entity was replaced
    assert saved.getId().equals(testRecipe.getId());
    assert saved.getName().equals("Updated Premium Chocolate Cake");
    assert saved.getTotalCost().doubleValue() == 25.75;
    assert saved.getIngredients().size() == 2;

    // Verify ingredients were updated
    List<RecipeIngredient> savedIngredients = saved.getIngredients();
    assert savedIngredients.get(0).getIngredientId().getValue().equals("ingredient-3");
    assert savedIngredients.get(0).getQuantity() == 300.0;
    assert savedIngredients.get(0).getUnit() == Unit.G;

    // Verify in database
    Optional<Recipe> found = recipeRepository.findById(testRecipe.getId());
    assert found.isPresent();
    Recipe foundRecipe = found.get();
    assert foundRecipe.getName().equals("Updated Premium Chocolate Cake");
    assert foundRecipe.getTotalCost().doubleValue() == 25.75;
    assert foundRecipe.getIngredients().size() == 2;
  }

  @Test
  void deleteById_shouldRemoveRecipeFromDatabase() {
    // Given
    recipeRepository.save(testRecipe);
    assert recipeRepository.findById(testRecipe.getId()).isPresent();

    // When
    recipeRepository.deleteById(testRecipe.getId());

    // Then
    Optional<Recipe> found = recipeRepository.findById(testRecipe.getId());
    assert found.isEmpty();
  }

  @Test
  void save_shouldHandleDifferentIngredientUnits() {
    // Create test ingredients first
    ingredientRepository.save(new Ingredient(Id.of("flour-id"), "Flour", 1000.0, Money.of(3.0), Unit.G));
    ingredientRepository.save(new Ingredient(Id.of("milk-id"), "Milk", 1000.0, Money.of(2.5), Unit.ML));
    ingredientRepository.save(new Ingredient(Id.of("sugar-id"), "Sugar", 1.0, Money.of(4.0), Unit.KG));
    ingredientRepository.save(new Ingredient(Id.of("eggs-id"), "Eggs", 12.0, Money.of(5.0), Unit.UN));
    ingredientRepository.save(new Ingredient(Id.of("oil-id"), "Oil", 1.0, Money.of(6.0), Unit.L));
    
    // Test with recipe using different units
    List<RecipeIngredient> mixedIngredients = List.of(
        new RecipeIngredient(Id.of("flour-id"), 250.0, Unit.G),
        new RecipeIngredient(Id.of("milk-id"), 500.0, Unit.ML),
        new RecipeIngredient(Id.of("sugar-id"), 0.2, Unit.KG),
        new RecipeIngredient(Id.of("eggs-id"), 3.0, Unit.UN),
        new RecipeIngredient(Id.of("oil-id"), 0.1, Unit.L)
    );

    Recipe mixedUnitsRecipe = new Recipe(
        Id.of("mixed-units-recipe"),
        "Mixed Units Cake",
        mixedIngredients,
        Money.of(18.90)
    );

    // Save
    Recipe saved = recipeRepository.save(mixedUnitsRecipe);

    // Verify units are preserved
    List<RecipeIngredient> savedIngredients = saved.getIngredients();
    assert savedIngredients.get(0).getUnit() == Unit.G;
    assert savedIngredients.get(1).getUnit() == Unit.ML;
    assert savedIngredients.get(2).getUnit() == Unit.KG;
    assert savedIngredients.get(3).getUnit() == Unit.UN;
    assert savedIngredients.get(4).getUnit() == Unit.L;
  }

  @Test
  void save_shouldHandleRecipeWithSingleIngredient() {
    // Create test ingredient first
    ingredientRepository.save(new Ingredient(Id.of("simple-ingredient"), "Simple Item", 1.0, Money.of(5.0), Unit.UN));
    
    // Given - recipe with single ingredient
    List<RecipeIngredient> singleIngredient = List.of(
        new RecipeIngredient(Id.of("simple-ingredient"), 1.0, Unit.UN)
    );

    Recipe simpleRecipe = new Recipe(
        Id.of("simple-recipe"),
        "Simple Recipe",
        singleIngredient,
        Money.of(5.00)
    );

    // When
    Recipe saved = recipeRepository.save(simpleRecipe);

    // Then
    assert saved.getIngredients().size() == 1;
    assert saved.getIngredients().get(0).getIngredientId().getValue().equals("simple-ingredient");
    assert saved.getIngredients().get(0).getQuantity() == 1.0;
    assert saved.getIngredients().get(0).getUnit() == Unit.UN;
  }

  @Test
  void save_shouldHandleZeroCostRecipe() {
    // Create test ingredient first
    ingredientRepository.save(new Ingredient(Id.of("free-ingredient"), "Free Item", 1.0, Money.of(0.0), Unit.G));
    
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

    // Verify after retrieval from database
    Optional<Recipe> found = recipeRepository.findById(freeRecipe.getId());
    assert found.isPresent();
    assert found.get().getTotalCost().doubleValue() == 0.0;
  }

  @Test
  void save_shouldHandlePreciseQuantitiesAndCosts() {
    // Create test ingredient first
    ingredientRepository.save(new Ingredient(Id.of("precise-ingredient"), "Precise Item", 1000.0, Money.of(10.0), Unit.G));
    
    // Given - recipe with precise values
    List<RecipeIngredient> preciseIngredients = List.of(
        new RecipeIngredient(Id.of("precise-ingredient"), 123.456, Unit.G)
    );

    Recipe preciseRecipe = new Recipe(
        Id.of("precise-recipe"),
        "Precise Recipe",
        preciseIngredients,
        Money.of(12.99) // Precise cost
    );

    // When
    Recipe saved = recipeRepository.save(preciseRecipe);

    // Then - precision should be maintained
    assert saved.getTotalCost().doubleValue() == 12.99;
    assert saved.getIngredients().get(0).getQuantity() == 123.456;

    // Verify after database round-trip
    Optional<Recipe> found = recipeRepository.findById(preciseRecipe.getId());
    assert found.isPresent();
    assert found.get().getTotalCost().doubleValue() == 12.99;
    assert found.get().getIngredients().get(0).getQuantity() == 123.456;
  }

  @Test
  void existsByName_shouldBeCaseExactMatch() {
    // Given
    recipeRepository.save(testRecipe); // "Chocolate Cake"

    // When/Then - exact match
    assert recipeRepository.existsByName("Chocolate Cake");

    // When/Then - case mismatch should not match (depends on DB collation)
    assert !recipeRepository.existsByName("chocolate cake");
    assert !recipeRepository.existsByName("CHOCOLATE CAKE");
  }
}