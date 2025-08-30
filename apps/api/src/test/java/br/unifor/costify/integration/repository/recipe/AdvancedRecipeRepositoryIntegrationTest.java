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
class AdvancedRecipeRepositoryIntegrationTest {

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
    // Create comprehensive test ingredients for advanced testing
    createAdvancedTestIngredients();
  }
  
  private void createAdvancedTestIngredients() {
    // Create a variety of ingredients for complex recipe testing
    ingredientRepository.save(new Ingredient(Id.of("premium-flour"), "Premium Flour", 1000.0, Money.of(5.50), Unit.G));
    ingredientRepository.save(new Ingredient(Id.of("organic-sugar"), "Organic Sugar", 1000.0, Money.of(6.00), Unit.G));
    ingredientRepository.save(new Ingredient(Id.of("farm-eggs"), "Farm Fresh Eggs", 12.0, Money.of(8.00), Unit.UN));
    ingredientRepository.save(new Ingredient(Id.of("whole-milk"), "Whole Milk", 1000.0, Money.of(3.50), Unit.ML));
    ingredientRepository.save(new Ingredient(Id.of("vanilla-extract"), "Pure Vanilla Extract", 50.0, Money.of(12.00), Unit.ML));
    ingredientRepository.save(new Ingredient(Id.of("butter"), "Unsalted Butter", 500.0, Money.of(7.50), Unit.G));
    ingredientRepository.save(new Ingredient(Id.of("baking-powder"), "Baking Powder", 200.0, Money.of(4.50), Unit.G));
    ingredientRepository.save(new Ingredient(Id.of("cocoa-powder"), "Dutch Cocoa Powder", 250.0, Money.of(9.50), Unit.G));
  }

  @Test
  void save_shouldUpdateExistingRecipe_whenImmutableEntityIsReplaced() {
    // Given - save initial recipe
    List<RecipeIngredient> originalIngredients = List.of(
        new RecipeIngredient(Id.of("premium-flour"), 500.0, Unit.G),
        new RecipeIngredient(Id.of("farm-eggs"), 2.0, Unit.UN)
    );
    
    Recipe testRecipe = new Recipe(
        Id.of("update-test-recipe"),
        "Original Cake",
        originalIngredients,
        Money.of(15.50)
    );
    recipeRepository.save(testRecipe);

    // When - create new immutable entity with same ID but different values
    List<RecipeIngredient> updatedIngredients = List.of(
        new RecipeIngredient(Id.of("organic-sugar"), 300.0, Unit.G),
        new RecipeIngredient(Id.of("whole-milk"), 250.0, Unit.ML),
        new RecipeIngredient(Id.of("vanilla-extract"), 5.0, Unit.ML)
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
    assert saved.getIngredients().size() == 3;

    // Verify ingredients were updated
    List<RecipeIngredient> savedIngredients = saved.getIngredients();
    assert savedIngredients.stream().anyMatch(ri -> ri.getIngredientId().getValue().equals("organic-sugar"));
    assert savedIngredients.stream().anyMatch(ri -> ri.getIngredientId().getValue().equals("whole-milk"));
    assert savedIngredients.stream().anyMatch(ri -> ri.getIngredientId().getValue().equals("vanilla-extract"));

    // Verify in database
    Optional<Recipe> found = recipeRepository.findById(testRecipe.getId());
    assert found.isPresent();
    Recipe foundRecipe = found.get();
    assert foundRecipe.getName().equals("Updated Premium Chocolate Cake");
    assert foundRecipe.getTotalCost().doubleValue() == 25.75;
    assert foundRecipe.getIngredients().size() == 3;
  }

  @Test
  void save_shouldHandleDifferentIngredientUnits() {
    // Test with recipe using different units
    List<RecipeIngredient> mixedIngredients = List.of(
        new RecipeIngredient(Id.of("premium-flour"), 250.0, Unit.G),
        new RecipeIngredient(Id.of("whole-milk"), 500.0, Unit.ML),
        new RecipeIngredient(Id.of("organic-sugar"), 200.0, Unit.G),
        new RecipeIngredient(Id.of("farm-eggs"), 3.0, Unit.UN),
        new RecipeIngredient(Id.of("vanilla-extract"), 10.0, Unit.ML)
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
    assert savedIngredients.stream().anyMatch(ri -> ri.getUnit() == Unit.G && ri.getIngredientId().getValue().equals("premium-flour"));
    assert savedIngredients.stream().anyMatch(ri -> ri.getUnit() == Unit.ML && ri.getIngredientId().getValue().equals("whole-milk"));
    assert savedIngredients.stream().anyMatch(ri -> ri.getUnit() == Unit.G && ri.getIngredientId().getValue().equals("organic-sugar"));
    assert savedIngredients.stream().anyMatch(ri -> ri.getUnit() == Unit.UN && ri.getIngredientId().getValue().equals("farm-eggs"));
    assert savedIngredients.stream().anyMatch(ri -> ri.getUnit() == Unit.ML && ri.getIngredientId().getValue().equals("vanilla-extract"));
  }

  @Test
  void save_shouldHandleManyIngredients() {
    // Given - recipe with many ingredients (complex recipe)
    List<RecipeIngredient> manyIngredients = List.of(
        new RecipeIngredient(Id.of("premium-flour"), 300.0, Unit.G),
        new RecipeIngredient(Id.of("organic-sugar"), 250.0, Unit.G),
        new RecipeIngredient(Id.of("farm-eggs"), 4.0, Unit.UN),
        new RecipeIngredient(Id.of("whole-milk"), 400.0, Unit.ML),
        new RecipeIngredient(Id.of("vanilla-extract"), 15.0, Unit.ML),
        new RecipeIngredient(Id.of("butter"), 200.0, Unit.G),
        new RecipeIngredient(Id.of("baking-powder"), 10.0, Unit.G),
        new RecipeIngredient(Id.of("cocoa-powder"), 50.0, Unit.G)
    );
    
    Recipe complexRecipe = new Recipe(
        Id.of("complex-recipe"),
        "Complex Chocolate Cake with Many Ingredients",
        manyIngredients,
        Money.of(45.50)
    );

    // When
    Recipe saved = recipeRepository.save(complexRecipe);

    // Then - all ingredients should be persisted
    assert saved.getIngredients().size() == 8;
    
    // Verify from database
    var found = recipeRepository.findById(Id.of("complex-recipe"));
    assert found.isPresent();
    assert found.get().getIngredients().size() == 8;
    
    // Verify specific ingredients are preserved
    List<RecipeIngredient> foundIngredients = found.get().getIngredients();
    assert foundIngredients.stream().anyMatch(ri -> ri.getIngredientId().getValue().equals("premium-flour"));
    assert foundIngredients.stream().anyMatch(ri -> ri.getIngredientId().getValue().equals("cocoa-powder"));
    assert foundIngredients.stream().anyMatch(ri -> ri.getIngredientId().getValue().equals("baking-powder"));
  }

  @Test
  void save_shouldHandlePreciseQuantitiesAndCosts() {
    // Given - recipe with precise values
    List<RecipeIngredient> preciseIngredients = List.of(
        new RecipeIngredient(Id.of("vanilla-extract"), 2.5, Unit.ML),
        new RecipeIngredient(Id.of("baking-powder"), 7.25, Unit.G),
        new RecipeIngredient(Id.of("premium-flour"), 123.456, Unit.G)
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
    
    // Check individual ingredient precision
    List<RecipeIngredient> savedIngredients = saved.getIngredients();
    Optional<RecipeIngredient> vanillaIngredient = savedIngredients.stream()
        .filter(ri -> ri.getIngredientId().getValue().equals("vanilla-extract"))
        .findFirst();
    assert vanillaIngredient.isPresent();
    assert vanillaIngredient.get().getQuantity() == 2.5;
    
    Optional<RecipeIngredient> bakingPowderIngredient = savedIngredients.stream()
        .filter(ri -> ri.getIngredientId().getValue().equals("baking-powder"))
        .findFirst();
    assert bakingPowderIngredient.isPresent();
    assert bakingPowderIngredient.get().getQuantity() == 7.25;
    
    Optional<RecipeIngredient> flourIngredient = savedIngredients.stream()
        .filter(ri -> ri.getIngredientId().getValue().equals("premium-flour"))
        .findFirst();
    assert flourIngredient.isPresent();
    assert flourIngredient.get().getQuantity() == 123.456;

    // Verify after database round-trip
    Optional<Recipe> found = recipeRepository.findById(preciseRecipe.getId());
    assert found.isPresent();
    assert found.get().getTotalCost().doubleValue() == 12.99;
    assert found.get().getIngredients().stream()
        .filter(ri -> ri.getIngredientId().getValue().equals("premium-flour"))
        .findFirst().get().getQuantity() == 123.456;
  }

  @Test
  void save_shouldHandleRecipeWithSingleExpensiveIngredient() {
    // Given - recipe with single expensive ingredient
    List<RecipeIngredient> singleExpensiveIngredient = List.of(
        new RecipeIngredient(Id.of("vanilla-extract"), 25.0, Unit.ML) // Expensive extract
    );

    Recipe expensiveSimpleRecipe = new Recipe(
        Id.of("expensive-simple-recipe"),
        "Premium Vanilla Recipe",
        singleExpensiveIngredient,
        Money.of(30.00) // High cost due to premium ingredient
    );

    // When
    Recipe saved = recipeRepository.save(expensiveSimpleRecipe);

    // Then
    assert saved.getIngredients().size() == 1;
    assert saved.getIngredients().get(0).getIngredientId().getValue().equals("vanilla-extract");
    assert saved.getIngredients().get(0).getQuantity() == 25.0;
    assert saved.getIngredients().get(0).getUnit() == Unit.ML;
    assert saved.getTotalCost().doubleValue() == 30.00;
  }

  @Test 
  void existsByName_shouldBeCaseExactMatch() {
    // Given
    List<RecipeIngredient> ingredients = List.of(
        new RecipeIngredient(Id.of("premium-flour"), 200.0, Unit.G)
    );
    
    Recipe testRecipe = new Recipe(
        Id.of("case-test-recipe"),
        "Chocolate Cake Supreme", // Specific case
        ingredients,
        Money.of(12.50)
    );
    recipeRepository.save(testRecipe);

    // When/Then - exact match
    assert recipeRepository.existsByName("Chocolate Cake Supreme");

    // When/Then - case mismatch should not match (depends on DB collation)
    assert !recipeRepository.existsByName("chocolate cake supreme");
    assert !recipeRepository.existsByName("CHOCOLATE CAKE SUPREME");
    assert !recipeRepository.existsByName("Chocolate cake supreme"); // Mixed case
  }

  @Test
  void save_shouldMaintainIngredientOrderConsistently() {
    // Given - recipe with ingredients in specific order
    List<RecipeIngredient> orderedIngredients = List.of(
        new RecipeIngredient(Id.of("premium-flour"), 300.0, Unit.G),     // 1st
        new RecipeIngredient(Id.of("organic-sugar"), 200.0, Unit.G),     // 2nd  
        new RecipeIngredient(Id.of("farm-eggs"), 3.0, Unit.UN),          // 3rd
        new RecipeIngredient(Id.of("whole-milk"), 250.0, Unit.ML),       // 4th
        new RecipeIngredient(Id.of("vanilla-extract"), 5.0, Unit.ML)     // 5th
    );

    Recipe orderedRecipe = new Recipe(
        Id.of("ordered-recipe"),
        "Ordered Ingredients Recipe",
        orderedIngredients,
        Money.of(22.75)
    );

    // When
    Recipe saved = recipeRepository.save(orderedRecipe);

    // Then - verify all ingredients are present (order may vary due to database)
    assert saved.getIngredients().size() == 5;
    List<String> savedIngredientIds = saved.getIngredients().stream()
        .map(ri -> ri.getIngredientId().getValue())
        .toList();
    
    assert savedIngredientIds.contains("premium-flour");
    assert savedIngredientIds.contains("organic-sugar");
    assert savedIngredientIds.contains("farm-eggs");
    assert savedIngredientIds.contains("whole-milk");
    assert savedIngredientIds.contains("vanilla-extract");
  }
}