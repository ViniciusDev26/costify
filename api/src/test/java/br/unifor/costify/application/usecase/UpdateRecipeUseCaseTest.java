package br.unifor.costify.application.usecase;

import br.unifor.costify.application.contracts.IngredientRepository;
import br.unifor.costify.application.contracts.RecipeRepository;
import br.unifor.costify.application.dto.command.UpdateRecipeCommand;
import br.unifor.costify.application.dto.entity.RecipeDto;
import br.unifor.costify.application.errors.RecipeNotFoundException;
import br.unifor.costify.domain.entity.Ingredient;
import br.unifor.costify.domain.entity.Recipe;
import br.unifor.costify.domain.service.RecipeCostCalculationService;
import br.unifor.costify.domain.valueobject.Id;
import br.unifor.costify.domain.valueobject.Money;
import br.unifor.costify.domain.valueobject.RecipeIngredient;
import br.unifor.costify.domain.valueobject.Unit;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("UpdateRecipeUseCase Tests")
class UpdateRecipeUseCaseTest {

    @Mock
    private RecipeRepository recipeRepository;

    @Mock
    private IngredientRepository ingredientRepository;

    @Mock
    private RecipeCostCalculationService costCalculationService;

    private UpdateRecipeUseCase useCase;

    @BeforeEach
    void setUp() {
        useCase = new UpdateRecipeUseCase(recipeRepository, ingredientRepository, costCalculationService);
    }

    @Test
    @DisplayName("Should update recipe successfully")
    void shouldUpdateRecipeSuccessfully() {
        // Arrange
        var recipeId = Id.of("recipe-123");
        var ingredientId = Id.of("ingredient-456");

        var existingRecipe = new Recipe(
                recipeId,
                "Bolo Original",
                List.of(new RecipeIngredient(ingredientId, 500.0, Unit.ML)),
                Money.of(new BigDecimal("10.00"))
        );

        var newIngredients = List.of(
                new RecipeIngredient(ingredientId, 600.0, Unit.ML)
        );

        var command = new UpdateRecipeCommand("Bolo Atualizado", newIngredients);

        var ingredient = new Ingredient(
                ingredientId,
                "Leite",
                1000.0,
                Money.of(new BigDecimal("5.00")),
                Unit.ML
        );

        var mockRecipeCost = new br.unifor.costify.domain.valueobject.RecipeCost(
                recipeId,
                "Bolo Atualizado",
                List.of(new br.unifor.costify.domain.valueobject.IngredientCost(
                        ingredientId,
                        "Leite",
                        600.0,
                        Unit.ML,
                        Money.of(new BigDecimal("12.00"))
                ))
        );

        when(recipeRepository.findById(recipeId)).thenReturn(Optional.of(existingRecipe));
        when(ingredientRepository.findById(ingredientId)).thenReturn(Optional.of(ingredient));
        when(costCalculationService.calculateCost(any(Recipe.class), any())).thenReturn(mockRecipeCost);
        when(recipeRepository.save(any(Recipe.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        RecipeDto result = useCase.execute(recipeId, command);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.name()).isEqualTo("Bolo Atualizado");
        assertThat(result.ingredients()).hasSize(1);
        assertThat(result.ingredients().get(0).quantity()).isEqualTo(600.0);
        assertThat(result.totalCost()).isEqualByComparingTo(new BigDecimal("12.00"));

        ArgumentCaptor<Recipe> recipeCaptor = ArgumentCaptor.forClass(Recipe.class);
        verify(recipeRepository).save(recipeCaptor.capture());
        Recipe savedRecipe = recipeCaptor.getValue();
        assertThat(savedRecipe.getName()).isEqualTo("Bolo Atualizado");
        assertThat(savedRecipe.getIngredients()).hasSize(1);
    }

    @Test
    @DisplayName("Should throw RecipeNotFoundException when recipe does not exist")
    void shouldThrowExceptionWhenRecipeNotFound() {
        // Arrange
        var recipeId = Id.of("non-existent-id");
        var command = new UpdateRecipeCommand(
                "Bolo",
                List.of(new RecipeIngredient(Id.of("ing-1"), 500.0, Unit.ML))
        );

        when(recipeRepository.findById(recipeId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> useCase.execute(recipeId, command))
                .isInstanceOf(RecipeNotFoundException.class)
                .hasMessage("Recipe with ID 'non-existent-id' not found");

        verify(recipeRepository).findById(recipeId);
    }

    @Test
    @DisplayName("Should update recipe with multiple ingredients")
    void shouldUpdateRecipeWithMultipleIngredients() {
        // Arrange
        var recipeId = Id.of("recipe-789");
        var ing1 = Id.of("ing-1");
        var ing2 = Id.of("ing-2");

        var existingRecipe = new Recipe(
                recipeId,
                "Receita Original",
                List.of(new RecipeIngredient(ing1, 100.0, Unit.G)),
                Money.of(new BigDecimal("5.00"))
        );

        var newIngredients = List.of(
                new RecipeIngredient(ing1, 200.0, Unit.G),
                new RecipeIngredient(ing2, 500.0, Unit.ML)
        );

        var command = new UpdateRecipeCommand("Receita Atualizada", newIngredients);

        var ingredient1 = new Ingredient(ing1, "Açúcar", 1000.0, Money.of(new BigDecimal("3.00")), Unit.G);
        var ingredient2 = new Ingredient(ing2, "Leite", 1000.0, Money.of(new BigDecimal("5.00")), Unit.ML);

        var mockRecipeCost = new br.unifor.costify.domain.valueobject.RecipeCost(
                recipeId,
                "Receita Atualizada",
                List.of(
                        new br.unifor.costify.domain.valueobject.IngredientCost(ing1, "Açúcar", 200.0, Unit.G, Money.of(new BigDecimal("0.60"))),
                        new br.unifor.costify.domain.valueobject.IngredientCost(ing2, "Leite", 500.0, Unit.ML, Money.of(new BigDecimal("2.50")))
                )
        );

        when(recipeRepository.findById(recipeId)).thenReturn(Optional.of(existingRecipe));
        when(ingredientRepository.findById(ing1)).thenReturn(Optional.of(ingredient1));
        when(ingredientRepository.findById(ing2)).thenReturn(Optional.of(ingredient2));
        when(costCalculationService.calculateCost(any(Recipe.class), any())).thenReturn(mockRecipeCost);
        when(recipeRepository.save(any(Recipe.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        RecipeDto result = useCase.execute(recipeId, command);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.ingredients()).hasSize(2);
        assertThat(result.totalCost()).isEqualByComparingTo(new BigDecimal("3.10"));
    }
}
