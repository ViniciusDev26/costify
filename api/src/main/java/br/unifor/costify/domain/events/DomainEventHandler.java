package br.unifor.costify.domain.events;

/**
 * Interface for handling domain events.
 * Implementations should process specific types of domain events.
 */
public interface DomainEventHandler {
  /**
   * Handle a domain event.
   *
   * @param event The domain event to handle
   */
  void handle(DomainEvent event);

  /**
   * Check if this handler can process the given event type.
   *
   * @param event The event to check
   * @return true if this handler can process the event, false otherwise
   */
  boolean canHandle(DomainEvent event);
}
