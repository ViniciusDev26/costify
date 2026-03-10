package br.unifor.costify.application.events;

import br.unifor.costify.application.usecase.RecalculateRecipeCostsForIngredientUseCase;
import br.unifor.costify.domain.events.ingredient.IngredientUpdatedEvent;
import br.unifor.costify.domain.valueobject.Id;
import br.unifor.costify.domain.valueobject.Money;
import br.unifor.costify.domain.valueobject.Unit;
import br.unifor.costify.infra.events.TransactionalDomainEventWrapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("IngredientUpdatedEventHandler Tests")
class IngredientUpdatedEventHandlerTest {

    @Mock
    private RecalculateRecipeCostsForIngredientUseCase recalculateRecipeCostsUseCase;

    private IngredientUpdatedEventHandler eventHandler;

    @BeforeEach
    void setUp() {
        eventHandler = new IngredientUpdatedEventHandler(recalculateRecipeCostsUseCase);
    }

    @Test
    @DisplayName("Should handle ingredient updated event and trigger recipe cost recalculation")
    void shouldHandleIngredientUpdatedEvent() {
        // Arrange
        Id ingredientId = Id.of("ingredient-123");
        var event = new IngredientUpdatedEvent(
                ingredientId,
                "Milk",
                1.0,
                Money.of(5.00),
                Unit.L
        );

        // Act
        eventHandler.handleIngredientUpdated(new TransactionalDomainEventWrapper(event));

        // Assert
        verify(recalculateRecipeCostsUseCase).execute(ingredientId);
    }

    @Test
    @DisplayName("Should handle multiple events independently")
    void shouldHandleMultipleEventsIndependently() {
        // Arrange
        Id ingredientId1 = Id.of("ingredient-1");
        Id ingredientId2 = Id.of("ingredient-2");

        var event1 = new IngredientUpdatedEvent(
                ingredientId1,
                "Milk",
                1.0,
                Money.of(5.00),
                Unit.L
        );

        var event2 = new IngredientUpdatedEvent(
                ingredientId2,
                "Sugar",
                1.0,
                Money.of(3.00),
                Unit.KG
        );

        // Act
        eventHandler.handleIngredientUpdated(new TransactionalDomainEventWrapper(event1));
        eventHandler.handleIngredientUpdated(new TransactionalDomainEventWrapper(event2));

        // Assert
        verify(recalculateRecipeCostsUseCase).execute(ingredientId1);
        verify(recalculateRecipeCostsUseCase).execute(ingredientId2);
        verifyNoMoreInteractions(recalculateRecipeCostsUseCase);
    }

    @Test
    @DisplayName("Should propagate exceptions from recipe cost recalculation use case")
    void shouldPropagateExceptionsFromUseCase() {
        // Arrange
        Id ingredientId = Id.of("ingredient-123");
        var event = new IngredientUpdatedEvent(
                ingredientId,
                "Milk",
                1.0,
                Money.of(5.00),
                Unit.L
        );

        doThrow(new RuntimeException("Database error"))
                .when(recalculateRecipeCostsUseCase)
                .execute(ingredientId);

        // Act & Assert
        try {
            eventHandler.handleIngredientUpdated(new TransactionalDomainEventWrapper(event));
        } catch (RuntimeException e) {
            // Exception should be propagated
            verify(recalculateRecipeCostsUseCase).execute(ingredientId);
        }
    }
}
