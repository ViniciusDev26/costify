package br.unifor.costify.application.service;

import br.unifor.costify.application.contracts.IngredientRepository;
import br.unifor.costify.application.contracts.RecipeRepository;
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
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("RecipeCostUpdateService Tests")
class RecipeCostUpdateServiceTest {

    @Mock
    private RecipeRepository recipeRepository;

    @Mock
    private IngredientRepository ingredientRepository;

    @Mock
    private RecipeCostCalculationService costCalculationService;

    private RecipeCostUpdateService recipeCostUpdateService;

    @BeforeEach
    void setUp() {
        recipeCostUpdateService = new RecipeCostUpdateService(
                recipeRepository,
                ingredientRepository,
                costCalculationService
        );
    }

    @Test
    @DisplayName("Should update costs of all recipes containing the updated ingredient")
    void shouldUpdateCostsOfRecipesContainingIngredient() {
        // Arrange
        Id ingredientId = Id.of("ingredient-123");

        var ingredient = new Ingredient(
                ingredientId,
                "Milk",
                1.0,
                Money.of(5.00),
                Unit.L
        );

        var recipe1 = new Recipe(
                Id.of("recipe-1"),
                "Cake",
                List.of(new RecipeIngredient(ingredientId, 0.5, Unit.L)),
                Money.of(5.00)
        );

        var recipe2 = new Recipe(
                Id.of("recipe-2"),
                "Smoothie",
                List.of(new RecipeIngredient(ingredientId, 0.3, Unit.L)),
                Money.of(3.00)
        );

        var recipesUsingIngredient = Arrays.asList(recipe1, recipe2);

        when(recipeRepository.findByIngredientId(ingredientId))
                .thenReturn(recipesUsingIngredient);
        when(ingredientRepository.findById(ingredientId))
                .thenReturn(Optional.of(ingredient));
        var ingredientCost1 = new IngredientCost(ingredientId, "Milk", 0.5, Unit.L, Money.of(10.00));
        var ingredientCost2 = new IngredientCost(ingredientId, "Milk", 0.3, Unit.L, Money.of(6.00));

        when(costCalculationService.calculateCost(eq(recipe1), anyMap()))
                .thenReturn(new RecipeCost(recipe1.getId(), recipe1.getName(), List.of(ingredientCost1)));
        when(costCalculationService.calculateCost(eq(recipe2), anyMap()))
                .thenReturn(new RecipeCost(recipe2.getId(), recipe2.getName(), List.of(ingredientCost2)));

        // Act
        recipeCostUpdateService.updateRecipeCostsForIngredient(ingredientId);

        // Assert
        verify(recipeRepository).findByIngredientId(ingredientId);
        verify(costCalculationService, times(2)).calculateCost(any(Recipe.class), anyMap());
        verify(recipeRepository).save(argThat(recipe ->
                recipe.getId().equals(Id.of("recipe-1")) &&
                recipe.getTotalCost().equals(Money.of(10.00))
        ));
        verify(recipeRepository).save(argThat(recipe ->
                recipe.getId().equals(Id.of("recipe-2")) &&
                recipe.getTotalCost().equals(Money.of(6.00))
        ));
    }

    @Test
    @DisplayName("Should do nothing when no recipes use the updated ingredient")
    void shouldDoNothingWhenNoRecipesUseIngredient() {
        // Arrange
        Id ingredientId = Id.of("ingredient-123");

        when(recipeRepository.findByIngredientId(ingredientId))
                .thenReturn(Collections.emptyList());

        // Act
        recipeCostUpdateService.updateRecipeCostsForIngredient(ingredientId);

        // Assert
        verify(recipeRepository).findByIngredientId(ingredientId);
        verify(costCalculationService, never()).calculateCost(any(), anyMap());
        verify(recipeRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should handle multiple ingredients in a recipe correctly")
    void shouldHandleMultipleIngredientsInRecipe() {
        // Arrange
        Id milkId = Id.of("ingredient-milk");
        Id sugarId = Id.of("ingredient-sugar");

        var milk = new Ingredient(
                milkId,
                "Milk",
                1.0,
                Money.of(5.00),
                Unit.L
        );

        var sugar = new Ingredient(
                sugarId,
                "Sugar",
                1.0,
                Money.of(3.00),
                Unit.KG
        );

        var recipe = new Recipe(
                Id.of("recipe-1"),
                "Cake",
                List.of(
                        new RecipeIngredient(milkId, 0.5, Unit.L),
                        new RecipeIngredient(sugarId, 0.2, Unit.KG)
                ),
                Money.of(10.00)
        );

        when(recipeRepository.findByIngredientId(milkId))
                .thenReturn(List.of(recipe));
        when(ingredientRepository.findById(milkId))
                .thenReturn(Optional.of(milk));
        when(ingredientRepository.findById(sugarId))
                .thenReturn(Optional.of(sugar));

        var ingredientCost1 = new IngredientCost(milkId, "Milk", 0.5, Unit.L, Money.of(2.50));
        var ingredientCost2 = new IngredientCost(sugarId, "Sugar", 0.2, Unit.KG, Money.of(0.60));

        when(costCalculationService.calculateCost(eq(recipe), anyMap()))
                .thenReturn(new RecipeCost(recipe.getId(), recipe.getName(), List.of(ingredientCost1, ingredientCost2)));

        // Act
        recipeCostUpdateService.updateRecipeCostsForIngredient(milkId);

        // Assert
        verify(recipeRepository).findByIngredientId(milkId);
        verify(costCalculationService).calculateCost(any(Recipe.class), anyMap());
        verify(recipeRepository).save(argThat(r ->
                r.getId().equals(Id.of("recipe-1")) &&
                r.getTotalCost().equals(Money.of(3.10))
        ));
    }
}
