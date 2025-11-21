package br.unifor.costify.application.usecase;

import br.unifor.costify.application.contracts.RecipeRepository;
import br.unifor.costify.application.dto.entity.RecipeDto;
import br.unifor.costify.application.errors.RecipeNotFoundException;
import br.unifor.costify.domain.entity.Recipe;
import br.unifor.costify.domain.valueobject.Id;
import br.unifor.costify.domain.valueobject.Money;
import br.unifor.costify.domain.valueobject.RecipeIngredient;
import br.unifor.costify.domain.valueobject.Unit;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("GetRecipeByIdUseCase Tests")
class GetRecipeByIdUseCaseTest {

    @Mock
    private RecipeRepository recipeRepository;

    private GetRecipeByIdUseCase useCase;

    @BeforeEach
    void setUp() {
        useCase = new GetRecipeByIdUseCase(recipeRepository);
    }

    @Test
    @DisplayName("Should get recipe by id successfully")
    void shouldGetRecipeByIdSuccessfully() {
        // Arrange
        var recipeId = Id.of("recipe-123");
        var ingredientId = Id.of("ingredient-456");

        var recipeIngredient = new RecipeIngredient(ingredientId, 500.0, Unit.ML);
        var recipe = new Recipe(
                recipeId,
                "Bolo de Chocolate",
                List.of(recipeIngredient),
                Money.of(new BigDecimal("10.50"))
        );

        when(recipeRepository.findById(recipeId)).thenReturn(Optional.of(recipe));

        // Act
        RecipeDto result = useCase.execute(recipeId.getValue());

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.id()).isEqualTo(recipeId.getValue());
        assertThat(result.name()).isEqualTo("Bolo de Chocolate");
        assertThat(result.ingredients()).hasSize(1);
        assertThat(result.ingredients().get(0).ingredientId()).isEqualTo(ingredientId.getValue());
        assertThat(result.ingredients().get(0).quantity()).isEqualTo(500.0);
        assertThat(result.ingredients().get(0).unit()).isEqualTo(Unit.ML);
        assertThat(result.totalCost()).isEqualByComparingTo(new BigDecimal("10.50"));

        verify(recipeRepository).findById(recipeId);
    }

    @Test
    @DisplayName("Should throw RecipeNotFoundException when recipe does not exist")
    void shouldThrowExceptionWhenRecipeNotFound() {
        // Arrange
        var recipeId = Id.of("non-existent-id");
        when(recipeRepository.findById(recipeId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> useCase.execute(recipeId.getValue()))
                .isInstanceOf(RecipeNotFoundException.class)
                .hasMessage("Recipe with ID 'non-existent-id' not found");

        verify(recipeRepository).findById(recipeId);
    }

    @Test
    @DisplayName("Should handle recipe with multiple ingredients")
    void shouldHandleRecipeWithMultipleIngredients() {
        // Arrange
        var recipeId = Id.of("recipe-789");
        var ingredient1 = Id.of("ing-1");
        var ingredient2 = Id.of("ing-2");
        var ingredient3 = Id.of("ing-3");

        var ingredients = List.of(
                new RecipeIngredient(ingredient1, 500.0, Unit.ML),
                new RecipeIngredient(ingredient2, 200.0, Unit.G),
                new RecipeIngredient(ingredient3, 2.0, Unit.UN)
        );

        var recipe = new Recipe(
                recipeId,
                "Receita Complexa",
                ingredients,
                Money.of(new BigDecimal("25.75"))
        );

        when(recipeRepository.findById(recipeId)).thenReturn(Optional.of(recipe));

        // Act
        RecipeDto result = useCase.execute(recipeId.getValue());

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.ingredients()).hasSize(3);
        assertThat(result.totalCost()).isEqualByComparingTo(new BigDecimal("25.75"));

        verify(recipeRepository).findById(recipeId);
    }
}
