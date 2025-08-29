package br.unifor.costify.domain.valueobject;

import br.unifor.costify.domain.entity.Ingredient;
import br.unifor.costify.domain.entity.Recipe;
import org.junit.jupiter.api.Test;

import java.util.List;

/**
 * Comprehensive validation tests for TBSP and TBSP_BUTTER units
 * ensuring they work correctly in all domain contexts
 */
public class TbspUnitsValidationTest {

    @Test
    void tbspUnit_shouldBeValidVolumeUnit() {
        // Given/When
        Unit tbsp = Unit.TBSP;
        
        // Then
        assert tbsp != null;
        assert tbsp.getType() == Unit.Type.VOLUME;
        assert tbsp.toBase(1.0) == 15.0; // 1 TBSP = 15ml
    }

    @Test
    void tbspButterUnit_shouldBeValidWeightUnit() {
        // Given/When
        Unit tbspButter = Unit.TBSP_BUTTER;
        
        // Then
        assert tbspButter != null;
        assert tbspButter.getType() == Unit.Type.WEIGHT;
        assert tbspButter.toBase(1.0) == 14.0; // 1 TBSP butter = 14g
    }

    @Test
    void tbspUnits_shouldWorkInIngredientCreation() {
        // Test TBSP (liquid) ingredient creation
        Ingredient vanillaExtract = new Ingredient(
            Id.of("vanilla-test"),
            "Vanilla Extract",
            10.0, // 10 tablespoons
            Money.of(8.50),
            Unit.TBSP
        );
        
        // Test TBSP_BUTTER (solid) ingredient creation
        Ingredient butter = new Ingredient(
            Id.of("butter-test"),
            "Butter",
            32.0, // 32 tablespoons
            Money.of(12.00),
            Unit.TBSP_BUTTER
        );
        
        // Verify ingredients are created successfully
        assert vanillaExtract != null;
        assert vanillaExtract.getPackageUnit() == Unit.TBSP;
        assert vanillaExtract.getPackageQuantity() == 10.0;
        
        assert butter != null;
        assert butter.getPackageUnit() == Unit.TBSP_BUTTER;
        assert butter.getPackageQuantity() == 32.0;
    }

    @Test
    void tbspUnits_shouldWorkInRecipeIngredients() {
        // Given
        Id vanillaId = Id.of("vanilla");
        Id butterId = Id.of("butter");
        
        // When - create recipe ingredients with TBSP units
        RecipeIngredient vanillaIngredient = new RecipeIngredient(vanillaId, 1.0, Unit.TBSP);
        RecipeIngredient butterIngredient = new RecipeIngredient(butterId, 2.0, Unit.TBSP_BUTTER);
        
        // Then
        assert vanillaIngredient != null;
        assert vanillaIngredient.getUnit() == Unit.TBSP;
        assert vanillaIngredient.getQuantity() == 1.0;
        
        assert butterIngredient != null;
        assert butterIngredient.getUnit() == Unit.TBSP_BUTTER;
        assert butterIngredient.getQuantity() == 2.0;
    }

    @Test
    void tbspUnits_shouldWorkInCompleteRecipe() {
        // Given
        Id recipeId = Id.of("tbsp-recipe");
        Id vanillaId = Id.of("vanilla");
        Id butterId = Id.of("butter");
        
        RecipeIngredient vanillaIngredient = new RecipeIngredient(vanillaId, 1.0, Unit.TBSP);
        RecipeIngredient butterIngredient = new RecipeIngredient(butterId, 2.0, Unit.TBSP_BUTTER);
        
        // When - create recipe with TBSP ingredients
        Recipe recipe = new Recipe(
            recipeId,
            "Vanilla Butter Cookie",
            List.of(vanillaIngredient, butterIngredient),
            Money.of(1.60)
        );
        
        // Then
        assert recipe != null;
        assert recipe.getIngredients().size() == 2;
        assert recipe.getIngredients().get(0).getUnit() == Unit.TBSP;
        assert recipe.getIngredients().get(1).getUnit() == Unit.TBSP_BUTTER;
    }

    @Test
    void tbspUnits_shouldCalculateCorrectUnitCosts() {
        // Test TBSP liquid ingredient unit cost calculation
        Ingredient vanillaExtract = new Ingredient(
            Id.of("vanilla-cost-test"),
            "Vanilla Extract",
            10.0, // 10 tablespoons
            Money.of(8.50), // $8.50 total
            Unit.TBSP
        );
        
        // Expected: $8.50 / (10 * 15ml) = $8.50 / 150ml = $0.0567 per ml
        double expectedVanillaCost = 8.50 / Unit.TBSP.toBase(10.0);
        assert Math.abs(vanillaExtract.getUnitCost() - expectedVanillaCost) < 0.0001;
        
        // Test TBSP_BUTTER ingredient unit cost calculation
        Ingredient butter = new Ingredient(
            Id.of("butter-cost-test"),
            "Butter",
            16.0, // 16 tablespoons
            Money.of(12.00), // $12.00 total
            Unit.TBSP_BUTTER
        );
        
        // Expected: $12.00 / (16 * 14g) = $12.00 / 224g = $0.0536 per g
        double expectedButterCost = 12.00 / Unit.TBSP_BUTTER.toBase(16.0);
        assert Math.abs(butter.getUnitCost() - expectedButterCost) < 0.0001;
    }

    @Test
    void tbspUnits_shouldHandleFractionalQuantities() {
        // Test half tablespoon measurements
        RecipeIngredient halfVanilla = new RecipeIngredient(Id.of("vanilla"), 0.5, Unit.TBSP);
        RecipeIngredient halfButter = new RecipeIngredient(Id.of("butter"), 0.5, Unit.TBSP_BUTTER);
        
        assert halfVanilla.getQuantity() == 0.5;
        assert halfButter.getQuantity() == 0.5;
        
        // Test conversion calculations
        assert Unit.TBSP.toBase(0.5) == 7.5; // 0.5 TBSP = 7.5ml
        assert Unit.TBSP_BUTTER.toBase(0.5) == 7.0; // 0.5 TBSP butter = 7g
    }

    @Test
    void tbspUnits_shouldHandleMultipleTablespoons() {
        // Test multiple tablespoon measurements (common in recipes)
        RecipeIngredient fourTbspVanilla = new RecipeIngredient(Id.of("vanilla"), 4.0, Unit.TBSP);
        RecipeIngredient eightTbspButter = new RecipeIngredient(Id.of("butter"), 8.0, Unit.TBSP_BUTTER);
        
        assert fourTbspVanilla.getQuantity() == 4.0;
        assert eightTbspButter.getQuantity() == 8.0;
        
        // Test conversion calculations
        assert Unit.TBSP.toBase(4.0) == 60.0; // 4 TBSP = 60ml = 1/4 cup
        assert Unit.TBSP_BUTTER.toBase(8.0) == 112.0; // 8 TBSP butter = 112g = 1/2 cup butter
    }

    @Test
    void tbspUnits_shouldBeCompatibleWithOtherUnits() {
        // Verify TBSP units can coexist with other units in recipes
        Id recipeId = Id.of("mixed-units-recipe");
        
        List<RecipeIngredient> mixedIngredients = List.of(
            new RecipeIngredient(Id.of("flour"), 500.0, Unit.G),      // Weight in grams
            new RecipeIngredient(Id.of("milk"), 250.0, Unit.ML),      // Volume in milliliters  
            new RecipeIngredient(Id.of("vanilla"), 1.0, Unit.TBSP),   // Volume in tablespoons
            new RecipeIngredient(Id.of("butter"), 4.0, Unit.TBSP_BUTTER), // Weight in tablespoon units
            new RecipeIngredient(Id.of("eggs"), 2.0, Unit.UN)         // Count units
        );
        
        Recipe mixedUnitsRecipe = new Recipe(
            recipeId,
            "Mixed Units Recipe",
            mixedIngredients,
            Money.of(15.75)
        );
        
        assert mixedUnitsRecipe != null;
        assert mixedUnitsRecipe.getIngredients().size() == 5;
        
        // Verify all units are preserved correctly
        List<Unit> actualUnits = mixedUnitsRecipe.getIngredients().stream()
            .map(RecipeIngredient::getUnit)
            .toList();
        
        assert actualUnits.contains(Unit.G);
        assert actualUnits.contains(Unit.ML);
        assert actualUnits.contains(Unit.TBSP);
        assert actualUnits.contains(Unit.TBSP_BUTTER);
        assert actualUnits.contains(Unit.UN);
    }

    @Test
    void tbspUnits_shouldSupportCommonCookingConversions() {
        // Common cooking conversions for validation
        
        // 1 cup liquid = 16 TBSP = 240ml
        double sixteenTbsp = Unit.TBSP.toBase(16.0);
        assert sixteenTbsp == 240.0; // 16 * 15ml = 240ml
        
        // 1/2 cup butter = 8 TBSP = 112g
        double eightTbspButter = Unit.TBSP_BUTTER.toBase(8.0);
        assert eightTbspButter == 112.0; // 8 * 14g = 112g
        
        // 1/4 cup liquid = 4 TBSP = 60ml
        double fourTbsp = Unit.TBSP.toBase(4.0);
        assert fourTbsp == 60.0; // 4 * 15ml = 60ml
        
        // 1 stick butter = 8 TBSP = 112g
        double oneStickButter = Unit.TBSP_BUTTER.toBase(8.0);
        assert oneStickButter == 112.0; // Same as 1/2 cup
    }
}