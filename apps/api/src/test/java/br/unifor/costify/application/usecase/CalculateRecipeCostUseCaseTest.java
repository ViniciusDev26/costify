package br.unifor.costify.application.usecase;

import br.unifor.costify.application.contracts.RecipeRepository;
import br.unifor.costify.application.dto.response.IngredientCostDto;
import br.unifor.costify.application.dto.response.RecipeCostDto;
import br.unifor.costify.application.errors.IngredientNotFoundException;
import br.unifor.costify.application.errors.RecipeNotFoundException;
import br.unifor.costify.application.service.IngredientLoaderService;
import br.unifor.costify.domain.entity.Ingredient;
import br.unifor.costify.domain.entity.Recipe;
import br.unifor.costify.domain.service.RecipeCostCalculationService;
import br.unifor.costify.domain.valueobject.Id;
import br.unifor.costify.domain.valueobject.IngredientCost;
import br.unifor.costify.domain.valueobject.Money;
import br.unifor.costify.domain.valueobject.RecipeCost;
import br.unifor.costify.domain.valueobject.RecipeIngredient;
import br.unifor.costify.domain.valueobject.Unit;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CalculateRecipeCostUseCaseTest {
    
    @Mock
    private RecipeRepository recipeRepository;
    
    @Mock
    private IngredientLoaderService ingredientLoaderService;
    
    @Mock
    private RecipeCostCalculationService costCalculationService;
    
    private CalculateRecipeCostUseCase useCase;
    
    @BeforeEach
    void setUp() {
        useCase = new CalculateRecipeCostUseCase(recipeRepository, ingredientLoaderService, costCalculationService);
    }
    
    @Test
    void shouldCalculateRecipeCostSuccessfully() {
        // Given
        String recipeIdString = "recipe-1";
        Id recipeId = Id.of(recipeIdString);
        Id ingredientId = Id.of("ingredient-1");
        
        RecipeIngredient recipeIngredient = new RecipeIngredient(ingredientId, 100.0, Unit.G);
        Recipe recipe = new Recipe(recipeId, "Chocolate Cake", List.of(recipeIngredient), Money.of(5.99));
        
        Ingredient ingredient = new Ingredient(ingredientId, "Flour", 1000.0, Money.of(5.0), Unit.G);
        
        IngredientCost ingredientCost = new IngredientCost(ingredientId, "Flour", 100.0, Unit.G, Money.of(0.5));
        RecipeCost recipeCost = new RecipeCost(recipeId, "Chocolate Cake", List.of(ingredientCost));
        
        when(recipeRepository.findById(recipeId)).thenReturn(Optional.of(recipe));
        when(ingredientLoaderService.loadIngredients(List.of(recipeIngredient))).thenReturn(Map.of(ingredientId, ingredient));
        when(costCalculationService.calculateCost(eq(recipe), any(Map.class))).thenReturn(recipeCost);
        
        // When
        RecipeCostDto result = useCase.execute(recipeIdString);
        
        // Then
        assertThat(result).isNotNull();
        assertThat(result.getRecipeId()).isEqualTo(recipeIdString);
        assertThat(result.getRecipeName()).isEqualTo("Chocolate Cake");
        assertThat(result.getTotalCost()).isEqualTo(BigDecimal.valueOf(0.50).setScale(2));
        assertThat(result.getIngredientCosts()).hasSize(1);
        assertThat(result.getIngredientCosts().get(0).getIngredientName()).isEqualTo("Flour");
        
        verify(recipeRepository).findById(recipeId);
        verify(ingredientLoaderService).loadIngredients(List.of(recipeIngredient));
        verify(costCalculationService).calculateCost(eq(recipe), any(Map.class));
    }
    
    @Test
    void shouldThrowRecipeNotFoundExceptionWhenRecipeDoesNotExist() {
        // Given
        String recipeIdString = "non-existent-recipe";
        Id recipeId = Id.of(recipeIdString);
        
        when(recipeRepository.findById(recipeId)).thenReturn(Optional.empty());
        
        // When / Then
        assertThatThrownBy(() -> useCase.execute(recipeIdString))
            .isInstanceOf(RecipeNotFoundException.class)
            .hasMessage("Recipe not found with ID: " + recipeIdString);
        
        verify(recipeRepository).findById(recipeId);
        verifyNoInteractions(ingredientLoaderService);
        verifyNoInteractions(costCalculationService);
    }
    
    @Test
    void shouldThrowIngredientNotFoundExceptionWhenIngredientDoesNotExist() {
        // Given
        String recipeIdString = "recipe-1";
        Id recipeId = Id.of(recipeIdString);
        Id ingredientId = Id.of("non-existent-ingredient");
        
        RecipeIngredient recipeIngredient = new RecipeIngredient(ingredientId, 100.0, Unit.G);
        Recipe recipe = new Recipe(recipeId, "Chocolate Cake", List.of(recipeIngredient), Money.of(5.99));
        
        when(recipeRepository.findById(recipeId)).thenReturn(Optional.of(recipe));
        when(ingredientLoaderService.loadIngredients(List.of(recipeIngredient))).thenThrow(new IngredientNotFoundException("Ingredient not found with ID: " + ingredientId));
        
        // When / Then
        assertThatThrownBy(() -> useCase.execute(recipeIdString))
            .isInstanceOf(IngredientNotFoundException.class)
            .hasMessage("Ingredient not found with ID: " + ingredientId);
        
        verify(recipeRepository).findById(recipeId);
        verify(ingredientLoaderService).loadIngredients(List.of(recipeIngredient));
        verifyNoInteractions(costCalculationService);
    }
    
    @Test
    void shouldCalculateCostForRecipeWithMultipleIngredients() {
        // Given
        String recipeIdString = "recipe-1";
        Id recipeId = Id.of(recipeIdString);
        Id ingredientId1 = Id.of("ingredient-1");
        Id ingredientId2 = Id.of("ingredient-2");
        
        RecipeIngredient recipeIngredient1 = new RecipeIngredient(ingredientId1, 100.0, Unit.G);
        RecipeIngredient recipeIngredient2 = new RecipeIngredient(ingredientId2, 2.0, Unit.UN);
        Recipe recipe = new Recipe(recipeId, "Chocolate Cake", List.of(recipeIngredient1, recipeIngredient2), Money.of(12.48));
        
        Ingredient ingredient1 = new Ingredient(ingredientId1, "Flour", 1000.0, Money.of(5.0), Unit.G);
        Ingredient ingredient2 = new Ingredient(ingredientId2, "Eggs", 12.0, Money.of(3.0), Unit.UN);
        
        IngredientCost ingredientCost1 = new IngredientCost(ingredientId1, "Flour", 100.0, Unit.G, Money.of(0.5));
        IngredientCost ingredientCost2 = new IngredientCost(ingredientId2, "Eggs", 2.0, Unit.UN, Money.of(0.5));
        RecipeCost recipeCost = new RecipeCost(recipeId, "Chocolate Cake", List.of(ingredientCost1, ingredientCost2));
        
        when(recipeRepository.findById(recipeId)).thenReturn(Optional.of(recipe));
        when(ingredientLoaderService.loadIngredients(List.of(recipeIngredient1, recipeIngredient2))).thenReturn(Map.of(
            ingredientId1, ingredient1,
            ingredientId2, ingredient2
        ));
        when(costCalculationService.calculateCost(eq(recipe), any(Map.class))).thenReturn(recipeCost);
        
        // When
        RecipeCostDto result = useCase.execute(recipeIdString);
        
        // Then
        assertThat(result).isNotNull();
        assertThat(result.getIngredientCosts()).hasSize(2);
        assertThat(result.getTotalCost()).isEqualTo(BigDecimal.valueOf(1.00).setScale(2));
        
        verify(ingredientLoaderService).loadIngredients(List.of(recipeIngredient1, recipeIngredient2));
    }
    
    @Test
    void shouldHandleEmptyRecipeGracefully() {
        // Given
        String recipeIdString = "empty-recipe";
        Id recipeId = Id.of(recipeIdString);
        
        // This should actually throw an exception in Recipe constructor due to validation
        // But let's test the use case behavior if somehow an empty recipe exists
        Recipe recipe = mock(Recipe.class);
        when(recipe.getIngredients()).thenReturn(Collections.emptyList());
        
        when(recipeRepository.findById(recipeId)).thenReturn(Optional.of(recipe));
        when(costCalculationService.calculateCost(eq(recipe), any(Map.class)))
            .thenThrow(new IllegalArgumentException("Recipe must have at least one ingredient"));
        
        // When / Then
        assertThatThrownBy(() -> useCase.execute(recipeIdString))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("Recipe must have at least one ingredient");
    }

    @Test
    void shouldCalculateCostForRecipeWithTbspUnits() {
        // Given
        String recipeIdString = "tbsp-recipe";
        Id recipeId = Id.of(recipeIdString);
        Id vanillaId = Id.of("vanilla-extract");
        Id butterId = Id.of("butter");
        
        // Recipe ingredients with TBSP units
        RecipeIngredient vanillaIngredient = new RecipeIngredient(vanillaId, 1.0, Unit.TBSP); // 1 tbsp vanilla
        RecipeIngredient butterIngredient = new RecipeIngredient(butterId, 2.0, Unit.TBSP_BUTTER); // 2 tbsp butter
        Recipe recipe = new Recipe(recipeId, "Vanilla Butter Cookie", List.of(vanillaIngredient, butterIngredient), Money.of(3.55));
        
        // Ingredients with TBSP package units
        Ingredient vanillaExtract = new Ingredient(vanillaId, "Vanilla Extract", 10.0, Money.of(8.50), Unit.TBSP); // 10 tbsp for $8.50
        Ingredient butter = new Ingredient(butterId, "Butter", 32.0, Money.of(12.00), Unit.TBSP_BUTTER); // 32 tbsp for $12.00
        
        // Expected costs:
        // - Vanilla: 1 tbsp out of 10 tbsp = $8.50 / 10 = $0.85
        // - Butter: 2 tbsp out of 32 tbsp = $12.00 / 32 * 2 = $0.75
        // - Total: $1.60
        IngredientCost vanillaCost = new IngredientCost(vanillaId, "Vanilla Extract", 1.0, Unit.TBSP, Money.of(0.85));
        IngredientCost butterCost = new IngredientCost(butterId, "Butter", 2.0, Unit.TBSP_BUTTER, Money.of(0.75));
        RecipeCost recipeCost = new RecipeCost(recipeId, "Vanilla Butter Cookie", List.of(vanillaCost, butterCost));
        
        when(recipeRepository.findById(recipeId)).thenReturn(Optional.of(recipe));
        when(ingredientLoaderService.loadIngredients(List.of(vanillaIngredient, butterIngredient))).thenReturn(Map.of(
            vanillaId, vanillaExtract,
            butterId, butter
        ));
        when(costCalculationService.calculateCost(eq(recipe), any(Map.class))).thenReturn(recipeCost);
        
        // When
        RecipeCostDto result = useCase.execute(recipeIdString);
        
        // Then
        assertThat(result).isNotNull();
        assertThat(result.getRecipeId()).isEqualTo(recipeIdString);
        assertThat(result.getRecipeName()).isEqualTo("Vanilla Butter Cookie");
        assertThat(result.getIngredientCosts()).hasSize(2);
        
        // Verify vanilla extract cost
        IngredientCostDto vanillaResult = result.getIngredientCosts().stream()
            .filter(cost -> cost.getIngredientId().equals(vanillaId.getValue()))
            .findFirst()
            .orElseThrow();
        assertThat(vanillaResult.getQuantityUsed()).isEqualTo(1.0);
        assertThat(vanillaResult.getUnit()).isEqualTo(Unit.TBSP);
        assertThat(vanillaResult.getCost()).isEqualTo(BigDecimal.valueOf(0.85).setScale(2, RoundingMode.HALF_UP));
        
        // Verify butter cost
        IngredientCostDto butterResult = result.getIngredientCosts().stream()
            .filter(cost -> cost.getIngredientId().equals(butterId.getValue()))
            .findFirst()
            .orElseThrow();
        assertThat(butterResult.getQuantityUsed()).isEqualTo(2.0);
        assertThat(butterResult.getUnit()).isEqualTo(Unit.TBSP_BUTTER);
        assertThat(butterResult.getCost()).isEqualTo(BigDecimal.valueOf(0.75).setScale(2, RoundingMode.HALF_UP));
        
        // Verify total cost
        assertThat(result.getTotalCost()).isEqualTo(BigDecimal.valueOf(1.60).setScale(2, RoundingMode.HALF_UP));
        
        verify(recipeRepository).findById(recipeId);
        verify(ingredientLoaderService).loadIngredients(List.of(vanillaIngredient, butterIngredient));
        verify(costCalculationService).calculateCost(eq(recipe), any(Map.class));
    }
}