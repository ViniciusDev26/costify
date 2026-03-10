package br.unifor.costify.integration.events;

import br.unifor.costify.TestcontainersConfiguration;
import br.unifor.costify.application.contracts.IngredientRepository;
import br.unifor.costify.application.contracts.RecipeRepository;
import br.unifor.costify.application.dto.command.UpdateIngredientCommand;
import br.unifor.costify.application.usecase.UpdateIngredientUseCase;
import br.unifor.costify.domain.contracts.IdGenerator;
import br.unifor.costify.domain.entity.Ingredient;
import br.unifor.costify.domain.entity.Recipe;
import br.unifor.costify.domain.valueobject.Id;
import br.unifor.costify.domain.valueobject.Money;
import br.unifor.costify.domain.valueobject.RecipeIngredient;
import br.unifor.costify.domain.valueobject.Unit;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * End-to-end integration test verifying that recipe costs are automatically
 * updated when ingredient prices change.
 *
 * This test demonstrates the complete event-driven flow:
 * 1. Create an ingredient and a recipe using that ingredient
 * 2. Update the ingredient price (triggers IngredientUpdatedEvent)
 * 3. Event handler updates recipe costs automatically
 * 4. Verify recipe total cost reflects the new ingredient price
 */
@SpringBootTest
@ActiveProfiles("dev")
@Import(TestcontainersConfiguration.class)
@DisplayName("Recipe Cost Update on Ingredient Change - Integration Test")
class RecipeCostUpdateOnIngredientChangeIntegrationTest {

  @Autowired
  private UpdateIngredientUseCase updateIngredientUseCase;

  @Autowired
  private IngredientRepository ingredientRepository;

  @Autowired
  private RecipeRepository recipeRepository;

  @Autowired
  private IdGenerator idGenerator;

  private Id milkId;
  private Id sugarId;
  private Id cakeRecipeId;
  private Id smoothieRecipeId;

  @BeforeEach
  void setUp() {
    // Create test ingredients
    Ingredient milk = new Ingredient(
        idGenerator,
        "Milk",
        1.0,  // 1 liter
        Money.of(5.0),  // $5.00 per liter
        Unit.L
    );
    Ingredient savedMilk = ingredientRepository.save(milk);
    milkId = savedMilk.getId();

    Ingredient sugar = new Ingredient(
        idGenerator,
        "Sugar",
        1.0,  // 1 kg
        Money.of(3.0),  // $3.00 per kg
        Unit.KG
    );
    Ingredient savedSugar = ingredientRepository.save(sugar);
    sugarId = savedSugar.getId();

    // Create recipe 1: Cake (uses 500ml milk = 0.5L and 200g sugar = 0.2kg)
    // Expected cost: (0.5 * 5.00) + (0.2 * 3.00) = 2.50 + 0.60 = 3.10
    Recipe cake = new Recipe(
        idGenerator,
        "Cake",
        List.of(
            new RecipeIngredient(milkId, 0.5, Unit.L),
            new RecipeIngredient(sugarId, 0.2, Unit.KG)
        ),
        Money.of(3.10)
    );
    Recipe savedCake = recipeRepository.save(cake);
    cakeRecipeId = savedCake.getId();

    // Create recipe 2: Smoothie (uses 300ml milk = 0.3L)
    // Expected cost: 0.3 * 5.00 = 1.50
    Recipe smoothie = new Recipe(
        idGenerator,
        "Smoothie",
        List.of(
            new RecipeIngredient(milkId, 0.3, Unit.L)
        ),
        Money.of(1.50)
    );
    Recipe savedSmoothie = recipeRepository.save(smoothie);
    smoothieRecipeId = savedSmoothie.getId();
  }

  @AfterEach
  void tearDown() {
    // Cleanup test data in correct order (recipes first, then ingredients)
    if (cakeRecipeId != null) {
      recipeRepository.deleteById(cakeRecipeId);
    }
    if (smoothieRecipeId != null) {
      recipeRepository.deleteById(smoothieRecipeId);
    }
    if (milkId != null) {
      ingredientRepository.deleteById(milkId);
    }
    if (sugarId != null) {
      ingredientRepository.deleteById(sugarId);
    }
  }

  @Test
  @DisplayName("Should update all recipe costs when ingredient price changes")
  void shouldUpdateAllRecipeCostsWhenIngredientPriceChanges() {
    // Arrange - Verify initial state
    Recipe cakeBefore = recipeRepository.findById(cakeRecipeId).orElseThrow();
    Recipe smoothieBefore = recipeRepository.findById(smoothieRecipeId).orElseThrow();

    assertEquals(Money.of(3.10), cakeBefore.getTotalCost(),
        "Initial cake cost should be $3.10");
    assertEquals(Money.of(1.50), smoothieBefore.getTotalCost(),
        "Initial smoothie cost should be $1.50");

    // Act - Update milk price from $5.00 to $8.00 per liter
    UpdateIngredientCommand updateMilkCommand = new UpdateIngredientCommand(
        "Milk",
        1.0,
        8.0,  // New price: $8.00 per liter
        Unit.L
    );
    updateIngredientUseCase.execute(milkId, updateMilkCommand);

    // Assert - Verify ingredient was updated
    Ingredient updatedMilk = ingredientRepository.findById(milkId).orElseThrow();
    assertEquals(Money.of(8.0), updatedMilk.getPackagePrice(),
        "Milk price should be updated to $8.00");

    // Assert - Verify recipe costs were automatically recalculated
    Recipe cakeAfter = recipeRepository.findById(cakeRecipeId).orElseThrow();
    Recipe smoothieAfter = recipeRepository.findById(smoothieRecipeId).orElseThrow();

    // Cake: (0.5L * $8.00) + (0.2kg * $3.00) = $4.00 + $0.60 = $4.60
    assertEquals(Money.of(4.60), cakeAfter.getTotalCost(),
        "Cake cost should be updated to $4.60 after milk price increase");

    // Smoothie: 0.3L * $8.00 = $2.40
    assertEquals(Money.of(2.40), smoothieAfter.getTotalCost(),
        "Smoothie cost should be updated to $2.40 after milk price increase");
  }

  @Test
  @DisplayName("Should update only affected recipes when ingredient changes")
  void shouldUpdateOnlyAffectedRecipes() {
    // Arrange - Get initial smoothie cost (uses milk)
    Recipe smoothieBefore = recipeRepository.findById(smoothieRecipeId).orElseThrow();
    assertEquals(Money.of(1.50), smoothieBefore.getTotalCost());

    // Act - Update sugar price (smoothie doesn't use sugar)
    UpdateIngredientCommand updateSugarCommand = new UpdateIngredientCommand(
        "Sugar",
        1.0,
        6.0,  // Double the price
        Unit.KG
    );
    updateIngredientUseCase.execute(sugarId, updateSugarCommand);

    // Assert - Smoothie cost should NOT change (it doesn't use sugar)
    Recipe smoothieAfter = recipeRepository.findById(smoothieRecipeId).orElseThrow();
    assertEquals(Money.of(1.50), smoothieAfter.getTotalCost(),
        "Smoothie cost should remain unchanged when sugar price changes");

    // Assert - Cake cost SHOULD change (it uses sugar)
    Recipe cakeAfter = recipeRepository.findById(cakeRecipeId).orElseThrow();
    // Cake: (0.5L * $5.00) + (0.2kg * $6.00) = $2.50 + $1.20 = $3.70
    assertEquals(Money.of(3.70), cakeAfter.getTotalCost(),
        "Cake cost should be updated to $3.70 after sugar price increase");
  }

  @Test
  @DisplayName("Should handle multiple sequential ingredient updates correctly")
  void shouldHandleMultipleSequentialUpdates() {
    // First update - increase milk price
    UpdateIngredientCommand updateMilk1 = new UpdateIngredientCommand(
        "Milk",
        1.0,
        10.0,
        Unit.L
    );
    updateIngredientUseCase.execute(milkId, updateMilk1);

    Recipe cakeAfter1 = recipeRepository.findById(cakeRecipeId).orElseThrow();
    // (0.5 * 10.00) + (0.2 * 3.00) = 5.00 + 0.60 = 5.60
    assertEquals(Money.of(5.60), cakeAfter1.getTotalCost(),
        "After first update, cake should cost $5.60");

    // Second update - increase sugar price
    UpdateIngredientCommand updateSugar = new UpdateIngredientCommand(
        "Sugar",
        1.0,
        4.0,
        Unit.KG
    );
    updateIngredientUseCase.execute(sugarId, updateSugar);

    Recipe cakeAfter2 = recipeRepository.findById(cakeRecipeId).orElseThrow();
    // (0.5 * 10.00) + (0.2 * 4.00) = 5.00 + 0.80 = 5.80
    assertEquals(Money.of(5.80), cakeAfter2.getTotalCost(),
        "After second update, cake should cost $5.80");

    // Third update - decrease milk price
    UpdateIngredientCommand updateMilk2 = new UpdateIngredientCommand(
        "Milk",
        1.0,
        4.0,
        Unit.L
    );
    updateIngredientUseCase.execute(milkId, updateMilk2);

    Recipe cakeAfter3 = recipeRepository.findById(cakeRecipeId).orElseThrow();
    // (0.5 * 4.00) + (0.2 * 4.00) = 2.00 + 0.80 = 2.80
    assertEquals(Money.of(2.80), cakeAfter3.getTotalCost(),
        "After third update, cake should cost $2.80");
  }

  @Test
  @DisplayName("Should handle partial ingredient updates correctly")
  void shouldHandlePartialIngredientUpdates() {
    // Arrange - Update only the ingredient name (price unchanged)
    UpdateIngredientCommand updateNameOnly = new UpdateIngredientCommand(
        "Whole Milk",  // Changed name
        null,  // Keep quantity
        null,  // Keep price at $5.00
        null   // Keep unit
    );

    // Act
    updateIngredientUseCase.execute(milkId, updateNameOnly);

    // Assert - Recipe costs should remain unchanged (price didn't change)
    Recipe cakeAfter = recipeRepository.findById(cakeRecipeId).orElseThrow();
    Recipe smoothieAfter = recipeRepository.findById(smoothieRecipeId).orElseThrow();

    assertEquals(Money.of(3.10), cakeAfter.getTotalCost(),
        "Cake cost should remain $3.10 when only ingredient name changes");
    assertEquals(Money.of(1.50), smoothieAfter.getTotalCost(),
        "Smoothie cost should remain $1.50 when only ingredient name changes");

    // Verify the name was actually updated
    Ingredient updatedMilk = ingredientRepository.findById(milkId).orElseThrow();
    assertEquals("Whole Milk", updatedMilk.getName(),
        "Ingredient name should be updated");
  }

  @Test
  @DisplayName("Should handle ingredient quantity and unit changes affecting unit cost")
  void shouldHandleQuantityAndUnitChanges() {
    // Arrange - Change milk packaging from 1L to 2L but keep same total price
    // This changes the unit cost from $5.00/L to $2.50/L
    UpdateIngredientCommand updatePackaging = new UpdateIngredientCommand(
        "Milk",
        2.0,  // Changed: now 2 liters per package
        5.0,  // Same price: $5.00 per package
        Unit.L
    );

    // Act
    updateIngredientUseCase.execute(milkId, updatePackaging);

    // Assert - Recipe costs should be recalculated with new unit cost
    Recipe cakeAfter = recipeRepository.findById(cakeRecipeId).orElseThrow();
    Recipe smoothieAfter = recipeRepository.findById(smoothieRecipeId).orElseThrow();

    // New unit cost: $5.00 / 2L = $2.50/L
    // Cake: (0.5L * $2.50) + (0.2kg * $3.00) = $1.25 + $0.60 = $1.85
    assertEquals(Money.of(1.85), cakeAfter.getTotalCost(),
        "Cake cost should be $1.85 with new unit cost");

    // Smoothie: 0.3L * $2.50 = $0.75
    assertEquals(Money.of(0.75), smoothieAfter.getTotalCost(),
        "Smoothie cost should be $0.75 with new unit cost");
  }
}
