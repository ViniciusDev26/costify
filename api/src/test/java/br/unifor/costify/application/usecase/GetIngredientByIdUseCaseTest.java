package br.unifor.costify.application.usecase;

import br.unifor.costify.application.contracts.IngredientRepository;
import br.unifor.costify.application.dto.entity.IngredientDto;
import br.unifor.costify.application.errors.IngredientNotFoundException;
import br.unifor.costify.domain.entity.Ingredient;
import br.unifor.costify.domain.valueobject.Id;
import br.unifor.costify.domain.valueobject.Money;
import br.unifor.costify.domain.valueobject.Unit;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("GetIngredientByIdUseCase Tests")
class GetIngredientByIdUseCaseTest {

    @Mock
    private IngredientRepository ingredientRepository;

    private GetIngredientByIdUseCase useCase;

    @BeforeEach
    void setUp() {
        useCase = new GetIngredientByIdUseCase(ingredientRepository);
    }

    @Test
    @DisplayName("Should get ingredient by id successfully")
    void shouldGetIngredientByIdSuccessfully() {
        // Arrange
        var ingredientId = Id.of("ingredient-123");
        var ingredient = new Ingredient(
                ingredientId,
                "Milk",
                1.0,
                Money.of(new BigDecimal("5.50")),
                Unit.L
        );

        when(ingredientRepository.findById(ingredientId)).thenReturn(Optional.of(ingredient));

        // Act
        IngredientDto result = useCase.execute(ingredientId.getValue());

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.id()).isEqualTo(ingredientId.getValue());
        assertThat(result.name()).isEqualTo("Milk");
        assertThat(result.packageQuantity()).isEqualTo(1.0);
        assertThat(result.packagePrice()).isEqualTo(5.50);
        assertThat(result.packageUnit()).isEqualTo(Unit.L);
        assertThat(result.unitCost()).isEqualTo(0.0055);

        verify(ingredientRepository).findById(ingredientId);
    }

    @Test
    @DisplayName("Should throw IngredientNotFoundException when ingredient does not exist")
    void shouldThrowExceptionWhenIngredientNotFound() {
        // Arrange
        var ingredientId = Id.of("non-existent-id");
        when(ingredientRepository.findById(ingredientId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> useCase.execute(ingredientId.getValue()))
                .isInstanceOf(IngredientNotFoundException.class)
                .hasMessage("Ingredient with ID 'non-existent-id' not found");

        verify(ingredientRepository).findById(ingredientId);
    }

    @Test
    @DisplayName("Should handle different units correctly")
    void shouldHandleDifferentUnitsCorrectly() {
        // Arrange
        var ingredientId = Id.of("ingredient-456");
        var ingredient = new Ingredient(
                ingredientId,
                "Sugar",
                1000.0,
                Money.of(new BigDecimal("10.00")),
                Unit.G
        );

        when(ingredientRepository.findById(ingredientId)).thenReturn(Optional.of(ingredient));

        // Act
        IngredientDto result = useCase.execute(ingredientId.getValue());

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.packageUnit()).isEqualTo(Unit.G);
        assertThat(result.unitCost()).isEqualTo(0.01);

        verify(ingredientRepository).findById(ingredientId);
    }
}
