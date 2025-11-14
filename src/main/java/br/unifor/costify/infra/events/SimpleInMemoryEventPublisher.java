package br.unifor.costify.infra.events;

import br.unifor.costify.domain.events.DomainEvent;
import br.unifor.costify.domain.events.DomainEventHandler;
import br.unifor.costify.domain.events.DomainEventPublisher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Simple in-memory implementation of DomainEventPublisher.
 * Uses a thread-safe list to manage subscribers and publishes events synchronously.
 */
@Component
public class SimpleInMemoryEventPublisher implements DomainEventPublisher {
  private static final Logger logger = LoggerFactory.getLogger(SimpleInMemoryEventPublisher.class);
  private final List<DomainEventHandler> handlers = new CopyOnWriteArrayList<>();

  @Override
  public void publish(DomainEvent event) {
    logger.debug("Publishing domain event: {} occurred at {}", event.getEventType(), event.getOccurredOn());

    for (DomainEventHandler handler : handlers) {
      if (handler.canHandle(event)) {
        try {
          handler.handle(event);
        } catch (Exception e) {
          logger.error("Error handling event {} with handler {}", event.getEventType(), handler.getClass().getSimpleName(), e);
        }
      }
    }
  }

  @Override
  public void subscribe(DomainEventHandler eventHandler) {
    handlers.add(eventHandler);
    logger.info("Subscribed handler: {}", eventHandler.getClass().getSimpleName());
  }

  @Override
  public void unsubscribe(DomainEventHandler eventHandler) {
    handlers.remove(eventHandler);
    logger.info("Unsubscribed handler: {}", eventHandler.getClass().getSimpleName());
  }
}
