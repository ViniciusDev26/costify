package br.unifor.costify.infra.events;

import br.unifor.costify.domain.events.DomainEvent;
import br.unifor.costify.domain.events.DomainEventHandler;
import br.unifor.costify.domain.events.DomainEventPublisher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

/**
 * Spring-based implementation of DomainEventPublisher.
 * Publishes domain events using Spring's ApplicationEventPublisher,
 * allowing events to be processed transactionally.
 *
 * This is the primary implementation used in production.
 * Events are only processed after transaction commit via @TransactionalEventListener.
 */
@Component
@Primary
public class SpringTransactionalEventPublisher implements DomainEventPublisher {
  private static final Logger logger = LoggerFactory.getLogger(SpringTransactionalEventPublisher.class);
  private final ApplicationEventPublisher springPublisher;

  public SpringTransactionalEventPublisher(ApplicationEventPublisher springPublisher) {
    this.springPublisher = springPublisher;
  }

  @Override
  public void publish(DomainEvent event) {
    logger.debug("Publishing domain event: {} occurred at {}", event.getEventType(), event.getOccurredOn());

    // Wrap the domain event and publish it through Spring
    // Spring's @TransactionalEventListener will ensure it's processed after commit
    springPublisher.publishEvent(new TransactionalDomainEventWrapper(event));
  }

  @Override
  public void subscribe(DomainEventHandler eventHandler) {
    logger.warn("Subscribe is not supported in SpringTransactionalEventPublisher. " +
        "Use @TransactionalEventListener beans instead.");
  }

  @Override
  public void unsubscribe(DomainEventHandler eventHandler) {
    logger.warn("Unsubscribe is not supported in SpringTransactionalEventPublisher. " +
        "Use Spring bean lifecycle instead.");
  }
}
