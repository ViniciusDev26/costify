package br.unifor.costify.infra.events.handlers;

import br.unifor.costify.domain.events.ingredient.IngredientUpdatedEvent;
import br.unifor.costify.infra.events.TransactionalDomainEventWrapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

/**
 * Event handler for ingredient-related domain events.
 * Uses Spring's @TransactionalEventListener to ensure events are processed
 * AFTER the database transaction commits successfully.
 *
 * This ensures that:
 * 1. Events are only processed if the transaction succeeds
 * 2. Event handlers see the committed state in the database
 * 3. If a handler fails, it doesn't roll back the original transaction
 */
@Component
public class IngredientEventHandler {
  private static final Logger logger = LoggerFactory.getLogger(IngredientEventHandler.class);

  /**
   * Handles IngredientUpdatedEvent after transaction commit.
   * This is where you would implement side effects like:
   * - Sending notifications
   * - Updating search indexes
   * - Triggering workflow processes
   * - Updating analytics/reporting databases
   * - Sending events to external systems
   *
   * @param wrapper The wrapped domain event from Spring's event bus
   */
  @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
  public void handleIngredientUpdated(TransactionalDomainEventWrapper wrapper) {
    if (wrapper.getEvent() instanceof IngredientUpdatedEvent event) {
      logger.info("Processing IngredientUpdatedEvent after commit: Ingredient {} updated to '{}'",
          event.getIngredientId().getValue(),
          event.getNewName());

      // Example side effects (implement as needed):
      // 1. Send notification to users
      // notificationService.notifyIngredientUpdate(event);

      // 2. Update search index
      // searchIndexService.updateIngredient(event.getIngredientId());

      // 3. Invalidate cache
      // cacheService.evictIngredient(event.getIngredientId());

      // 4. Audit logging
      // auditService.logIngredientUpdate(event);

      logger.debug("IngredientUpdatedEvent processed successfully for ingredient {}",
          event.getIngredientId().getValue());
    }
  }

  /**
   * Example: Handle events that should run BEFORE commit.
   * Use this phase carefully - if this handler throws an exception,
   * it will rollback the transaction.
   *
   * @param wrapper The wrapped domain event
   */
  @TransactionalEventListener(phase = TransactionPhase.BEFORE_COMMIT)
  public void validateIngredientUpdateBeforeCommit(TransactionalDomainEventWrapper wrapper) {
    if (wrapper.getEvent() instanceof IngredientUpdatedEvent event) {
      logger.debug("Validating ingredient update before commit: {}",
          event.getIngredientId().getValue());

      // Example validations that should fail the transaction:
      // - Business rule validations across aggregates
      // - External system checks that must succeed
      // Note: If this throws an exception, the transaction will rollback
    }
  }

  /**
   * Example: Handle events that should run AFTER completion (commit or rollback).
   * This runs regardless of transaction outcome.
   *
   * @param wrapper The wrapped domain event
   */
  @TransactionalEventListener(phase = TransactionPhase.AFTER_COMPLETION)
  public void cleanupAfterCompletion(TransactionalDomainEventWrapper wrapper) {
    if (wrapper.getEvent() instanceof IngredientUpdatedEvent event) {
      logger.trace("Cleanup after transaction completion for ingredient {}",
          event.getIngredientId().getValue());

      // Example cleanup tasks:
      // - Release resources
      // - Clear thread-local variables
      // - Send metrics regardless of success/failure
    }
  }
}
