package br.unifor.costify.domain.service;

import br.unifor.costify.domain.entity.Ingredient;
import br.unifor.costify.domain.entity.Recipe;
import br.unifor.costify.domain.valueobject.Id;
import br.unifor.costify.domain.valueobject.RecipeIngredient;
import br.unifor.costify.domain.valueobject.RecipeCost;
import br.unifor.costify.domain.valueobject.IngredientCost;
import br.unifor.costify.domain.valueobject.Money;
import br.unifor.costify.domain.valueobject.Unit;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class RecipeCostCalculationServiceTest {

    private RecipeCostCalculationService service;
    private Ingredient flour;
    private Ingredient sugar;
    private Recipe breadRecipe;

    @BeforeEach
    void setUp() {
        service = new RecipeCostCalculationService();
        
        // Create test ingredients
        // Flour: 1kg package costs $5.00, so per gram = $0.005
        flour = new Ingredient(
            Id.of("flour-id"),
            "Flour",
            1.0,    // 1kg package quantity
            Money.of(5.00),   // $5.00 package price
            Unit.KG  // package in kilograms
        );
        
        // Sugar: 500g package costs $3.00, so per gram = $0.006
        sugar = new Ingredient(
            Id.of("sugar-id"),
            "Sugar", 
            500.0,  // 500g package
            Money.of(3.00),   // $3.00 package price
            Unit.G   // package in grams
        );
        
        // Create recipe that needs 300g flour + 100g sugar
        breadRecipe = new Recipe(
            Id.of("bread-id"),
            "Simple Bread",
            Arrays.asList(
                new RecipeIngredient(flour.getId(), 300.0, Unit.G), // 300g flour
                new RecipeIngredient(sugar.getId(), 100.0, Unit.G)  // 100g sugar
            ),
            Money.of(10.0)
        );
    }

    @Test
    void shouldCalculateRecipeCostWithIngredientBreakdown() {
        // Arrange
        Map<Id, Ingredient> ingredientMap = Map.of(
            flour.getId(), flour,
            sugar.getId(), sugar
        );

        // Act
        RecipeCost result = service.calculateCost(breadRecipe, ingredientMap);

        // Assert
        assertNotNull(result);
        assertEquals(Money.of(2.10), result.getTotalCost()); // 300g * $0.005 + 100g * $0.006 = $1.50 + $0.60 = $2.10
        
        assertEquals(2, result.getIngredientCosts().size());
        
        // Check flour cost breakdown
        var flourCost = result.getIngredientCosts().stream()
            .filter(ic -> ic.getIngredientId().equals(flour.getId()))
            .findFirst().orElseThrow();
        assertEquals(Money.of(1.50), flourCost.getCost());
        assertEquals(300.0, flourCost.getQuantityUsed(), 0.01);
        assertEquals(Unit.G, flourCost.getUnit());
        
        // Check sugar cost breakdown  
        var sugarCost = result.getIngredientCosts().stream()
            .filter(ic -> ic.getIngredientId().equals(sugar.getId()))
            .findFirst().orElseThrow();
        assertEquals(Money.of(0.60), sugarCost.getCost());
        assertEquals(100.0, sugarCost.getQuantityUsed(), 0.01);
        assertEquals(Unit.G, sugarCost.getUnit());
    }

    @Test
    void shouldHandleUnitConversionInCostCalculation() {
        // Arrange - Recipe uses ML but ingredient package is in L
        Ingredient milk = new Ingredient(
            Id.of("milk-id"),
            "Milk",
            1.0,    // 1L package  
            Money.of(2.50),   // $2.50 package price
            Unit.L   // package in liters
        );
        
        Recipe milkshakeRecipe = new Recipe(
            Id.of("milkshake-id"),
            "Milkshake",
            List.of(new RecipeIngredient(milk.getId(), 250.0, Unit.ML)), // needs 250ml
            Money.of(8.0)
        );
        
        Map<Id, Ingredient> ingredientMap = Map.of(milk.getId(), milk);

        // Act
        RecipeCost result = service.calculateCost(milkshakeRecipe, ingredientMap);

        // Assert
        assertNotNull(result);
        // 250ml = 0.25L, cost should be 0.25 * $2.50 = $0.625
        assertEquals(Money.of(0.63), result.getTotalCost()); // Rounded to 2 decimal places
        
        var milkCost = result.getIngredientCosts().get(0);
        assertEquals(Money.of(0.63), milkCost.getCost());
        assertEquals(250.0, milkCost.getQuantityUsed(), 0.01);
        assertEquals(Unit.ML, milkCost.getUnit());
    }

    @Test
    void shouldThrowExceptionWhenIngredientNotFound() {
        // Arrange
        Map<Id, Ingredient> emptyMap = Map.of();

        // Act & Assert
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> service.calculateCost(breadRecipe, emptyMap)
        );
        
        assertTrue(exception.getMessage().contains("Ingredient not found"));
    }

    @Test
    void shouldThrowExceptionWhenRecipeIsNull() {
        // Act & Assert
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> service.calculateCost(null, Map.of())
        );
        
        assertEquals("Recipe cannot be null", exception.getMessage());
    }

    @Test
    void shouldThrowExceptionWhenIngredientMapIsNull() {
        // Act & Assert
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> service.calculateCost(breadRecipe, null)
        );
        
        assertEquals("Ingredient map cannot be null", exception.getMessage());
    }

    @Test
    void shouldCalculateCostForRecipeWithSameIngredientMultipleTimes() {
        // Arrange - Recipe that uses flour twice in different measurements
        Recipe complexRecipe = new Recipe(
            Id.of("complex-id"),
            "Complex Recipe",
            Arrays.asList(
                new RecipeIngredient(flour.getId(), 200.0, Unit.G), // 200g flour
                new RecipeIngredient(flour.getId(), 100.0, Unit.G), // another 100g flour
                new RecipeIngredient(sugar.getId(), 50.0, Unit.G)   // 50g sugar
            ),
            Money.of(15.0)
        );
        
        Map<Id, Ingredient> ingredientMap = Map.of(
            flour.getId(), flour,
            sugar.getId(), sugar
        );

        // Act
        RecipeCost result = service.calculateCost(complexRecipe, ingredientMap);

        // Assert
        assertNotNull(result);
        // Total flour: 300g * $0.005 = $1.50
        // Total sugar: 50g * $0.006 = $0.30
        // Total: $1.80
        assertEquals(Money.of(1.80), result.getTotalCost());
        
        // Should have separate entries for each ingredient usage
        assertEquals(3, result.getIngredientCosts().size());
    }
}