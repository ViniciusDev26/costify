package br.unifor.costify.application.events;

import br.unifor.costify.application.service.RecipeCostUpdateService;
import br.unifor.costify.domain.events.ingredient.IngredientUpdatedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

/**
 * Event handler that listens to IngredientUpdatedEvent and triggers
 * recipe cost updates for all recipes using the updated ingredient.
 * <p>
 * This handler uses TransactionalEventListener to ensure the event is processed
 * after the ingredient update transaction has been committed, preventing
 * inconsistent state and potential database deadlocks.
 */
@Component
public class IngredientUpdatedEventHandler {

    private final RecipeCostUpdateService recipeCostUpdateService;

    public IngredientUpdatedEventHandler(RecipeCostUpdateService recipeCostUpdateService) {
        this.recipeCostUpdateService = recipeCostUpdateService;
    }

    /**
     * Handles the IngredientUpdatedEvent after the transaction commits.
     * This method updates the costs of all recipes that use the updated ingredient.
     *
     * @param event the ingredient updated event containing the updated ingredient data
     */
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleIngredientUpdated(IngredientUpdatedEvent event) {
        recipeCostUpdateService.updateRecipeCostsForIngredient(event.getIngredientId());
    }
}
