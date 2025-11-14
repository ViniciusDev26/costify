package br.unifor.costify.domain.events;

import java.time.Instant;

/**
 * Base interface for all domain events.
 * Domain events represent significant occurrences within the domain that domain experts care about.
 */
public interface DomainEvent {
  /**
   * Get the timestamp when the event occurred.
   *
   * @return The instant when the event was created
   */
  Instant getOccurredOn();

  /**
   * Get the type of the event.
   *
   * @return The event type as a string
   */
  String getEventType();
}
