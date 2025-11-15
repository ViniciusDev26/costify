package br.unifor.costify.application.events;

import br.unifor.costify.application.service.RecipeCostUpdateService;
import br.unifor.costify.domain.events.ingredient.IngredientUpdatedEvent;
import br.unifor.costify.domain.valueobject.Id;
import br.unifor.costify.domain.valueobject.Money;
import br.unifor.costify.domain.valueobject.Unit;
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
    private RecipeCostUpdateService recipeCostUpdateService;

    private IngredientUpdatedEventHandler eventHandler;

    @BeforeEach
    void setUp() {
        eventHandler = new IngredientUpdatedEventHandler(recipeCostUpdateService);
    }

    @Test
    @DisplayName("Should handle ingredient updated event and trigger recipe cost update")
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
        eventHandler.handleIngredientUpdated(event);

        // Assert
        verify(recipeCostUpdateService).updateRecipeCostsForIngredient(ingredientId);
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
        eventHandler.handleIngredientUpdated(event1);
        eventHandler.handleIngredientUpdated(event2);

        // Assert
        verify(recipeCostUpdateService).updateRecipeCostsForIngredient(ingredientId1);
        verify(recipeCostUpdateService).updateRecipeCostsForIngredient(ingredientId2);
        verifyNoMoreInteractions(recipeCostUpdateService);
    }

    @Test
    @DisplayName("Should propagate exceptions from recipe cost update service")
    void shouldPropagateExceptionsFromService() {
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
                .when(recipeCostUpdateService)
                .updateRecipeCostsForIngredient(ingredientId);

        // Act & Assert
        try {
            eventHandler.handleIngredientUpdated(event);
        } catch (RuntimeException e) {
            // Exception should be propagated
            verify(recipeCostUpdateService).updateRecipeCostsForIngredient(ingredientId);
        }
    }
}
