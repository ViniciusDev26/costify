package br.unifor.costify.infra.events;

import br.unifor.costify.domain.events.DomainEvent;

import java.time.Instant;

/**
 * Wrapper for domain events to be published within Spring transactions.
 * This wrapper ensures that domain events are only processed after the transaction commits.
 */
public class TransactionalDomainEventWrapper {
  private final DomainEvent event;
  private final Instant wrapperCreatedAt;

  public TransactionalDomainEventWrapper(DomainEvent event) {
    this.event = event;
    this.wrapperCreatedAt = Instant.now();
  }

  public DomainEvent getEvent() {
    return event;
  }

  public Instant getWrapperCreatedAt() {
    return wrapperCreatedAt;
  }
}
