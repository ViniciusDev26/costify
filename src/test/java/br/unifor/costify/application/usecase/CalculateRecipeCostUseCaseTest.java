package br.unifor.costify.application.usecase;

import br.unifor.costify.application.contracts.IngredientRepository;
import br.unifor.costify.application.contracts.RecipeRepository;
import br.unifor.costify.application.dto.response.RecipeCostDto;
import br.unifor.costify.application.errors.IngredientNotFoundException;
import br.unifor.costify.application.errors.RecipeNotFoundException;
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
    private IngredientRepository ingredientRepository;
    
    @Mock
    private RecipeCostCalculationService costCalculationService;
    
    private CalculateRecipeCostUseCase useCase;
    
    @BeforeEach
    void setUp() {
        useCase = new CalculateRecipeCostUseCase(recipeRepository, ingredientRepository, costCalculationService);
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
        when(ingredientRepository.findById(ingredientId)).thenReturn(Optional.of(ingredient));
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
        verify(ingredientRepository).findById(ingredientId);
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
        verifyNoInteractions(ingredientRepository);
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
        when(ingredientRepository.findById(ingredientId)).thenReturn(Optional.empty());
        
        // When / Then
        assertThatThrownBy(() -> useCase.execute(recipeIdString))
            .isInstanceOf(IngredientNotFoundException.class)
            .hasMessage("Ingredient not found with ID: " + ingredientId);
        
        verify(recipeRepository).findById(recipeId);
        verify(ingredientRepository).findById(ingredientId);
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
        when(ingredientRepository.findById(ingredientId1)).thenReturn(Optional.of(ingredient1));
        when(ingredientRepository.findById(ingredientId2)).thenReturn(Optional.of(ingredient2));
        when(costCalculationService.calculateCost(eq(recipe), any(Map.class))).thenReturn(recipeCost);
        
        // When
        RecipeCostDto result = useCase.execute(recipeIdString);
        
        // Then
        assertThat(result).isNotNull();
        assertThat(result.getIngredientCosts()).hasSize(2);
        assertThat(result.getTotalCost()).isEqualTo(BigDecimal.valueOf(1.00).setScale(2));
        
        verify(ingredientRepository).findById(ingredientId1);
        verify(ingredientRepository).findById(ingredientId2);
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
}