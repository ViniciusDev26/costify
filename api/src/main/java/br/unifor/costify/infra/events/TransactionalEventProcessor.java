package br.unifor.costify.infra.events;

import br.unifor.costify.domain.events.DomainEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

/**
 * Processes domain events after transaction commits.
 * This ensures that events are only processed if the transaction was successful,
 * maintaining consistency between domain state and published events.
 */
@Component
public class TransactionalEventProcessor {
  private static final Logger logger = LoggerFactory.getLogger(TransactionalEventProcessor.class);

  /**
   * Process domain events after the transaction commits successfully.
   * If the transaction rolls back, this method will not be called.
   *
   * @param wrapper The wrapped domain event
   */
  @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
  public void handleAfterCommit(TransactionalDomainEventWrapper wrapper) {
    DomainEvent event = wrapper.getEvent();

    logger.info("Processing domain event after commit: {} (occurred at: {}, processed after: {}ms)",
        event.getEventType(),
        event.getOccurredOn(),
        java.time.Duration.between(event.getOccurredOn(), java.time.Instant.now()).toMillis());

    // Here you can add specific event handlers or forward to external systems
    // For example:
    // - Send to Kafka/RabbitMQ
    // - Update read models
    // - Trigger workflows
    // - Send notifications
  }

  /**
   * Handle events that failed to commit.
   * This can be used for compensation logic or alerting.
   *
   * @param wrapper The wrapped domain event
   */
  @TransactionalEventListener(phase = TransactionPhase.AFTER_ROLLBACK)
  public void handleAfterRollback(TransactionalDomainEventWrapper wrapper) {
    DomainEvent event = wrapper.getEvent();

    logger.warn("Transaction rolled back for event: {} (occurred at: {})",
        event.getEventType(),
        event.getOccurredOn());

    // Here you can add compensation logic or alerting
  }
}
