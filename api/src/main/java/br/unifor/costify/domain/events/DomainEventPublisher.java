package br.unifor.costify.domain.events;

/**
 * Interface for publishing domain events.
 * Implementations should handle the distribution of events to interested subscribers.
 */
public interface DomainEventPublisher {
  /**
   * Publish a domain event to all registered subscribers.
   *
   * @param event The domain event to publish
   */
  void publish(DomainEvent event);

  /**
   * Subscribe a handler to domain events.
   *
   * @param eventHandler The handler to be notified when events are published
   */
  void subscribe(DomainEventHandler eventHandler);

  /**
   * Remove a subscriber from the event publisher.
   *
   * @param eventHandler The handler to unsubscribe
   */
  void unsubscribe(DomainEventHandler eventHandler);
}
