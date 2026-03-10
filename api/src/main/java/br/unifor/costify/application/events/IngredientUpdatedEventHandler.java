package br.unifor.costify.application.events;

import br.unifor.costify.application.usecase.RecalculateRecipeCostsForIngredientUseCase;
import br.unifor.costify.domain.events.ingredient.IngredientUpdatedEvent;
import br.unifor.costify.infra.events.TransactionalDomainEventWrapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

/**
 * Event handler that listens to IngredientUpdatedEvent and triggers
 * recipe cost recalculation for all recipes using the updated ingredient.
 * <p>
 * This handler uses TransactionalEventListener to ensure the event is processed
 * after the ingredient update transaction has been committed, preventing
 * inconsistent state and potential database deadlocks.
 * <p>
 * This handler acts as an orchestrator that connects the UpdateIngredient use case
 * with the RecalculateRecipeCosts use case through domain events, maintaining
 * loose coupling between use cases.
 */
@Component
public class IngredientUpdatedEventHandler {
    private static final Logger logger = LoggerFactory.getLogger(IngredientUpdatedEventHandler.class);

    private final RecalculateRecipeCostsForIngredientUseCase recalculateRecipeCostsUseCase;

    public IngredientUpdatedEventHandler(RecalculateRecipeCostsForIngredientUseCase recalculateRecipeCostsUseCase) {
        this.recalculateRecipeCostsUseCase = recalculateRecipeCostsUseCase;
    }

    /**
     * Handles the IngredientUpdatedEvent after the transaction commits.
     * This method triggers the recalculation of costs for all recipes that use the updated ingredient.
     *
     * @param wrapper the transactional wrapper containing the ingredient updated event
     */
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleIngredientUpdated(TransactionalDomainEventWrapper wrapper) {
        logger.info("Received TransactionalDomainEventWrapper: {}", wrapper.getEvent().getClass().getSimpleName());
        // Unwrap the domain event
        if (wrapper.getEvent() instanceof IngredientUpdatedEvent event) {
            logger.info("Handling IngredientUpdatedEvent for ingredient: {}", event.getIngredientId().getValue());
            recalculateRecipeCostsUseCase.execute(event.getIngredientId());
            logger.info("Recipe costs recalculation triggered for ingredient: {}", event.getIngredientId().getValue());
        } else {
            logger.warn("Received unexpected event type: {}", wrapper.getEvent().getClass().getName());
        }
    }
}
