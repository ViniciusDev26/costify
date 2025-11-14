package br.unifor.costify.application.usecase;

import br.unifor.costify.application.contracts.IngredientRepository;
import br.unifor.costify.application.contracts.TransactionManager;
import br.unifor.costify.application.dto.command.UpdateIngredientCommand;
import br.unifor.costify.application.dto.entity.IngredientDto;
import br.unifor.costify.domain.entity.Ingredient;
import br.unifor.costify.domain.events.DomainEvent;
import br.unifor.costify.domain.events.DomainEventPublisher;
import br.unifor.costify.domain.valueobject.Id;
import br.unifor.costify.domain.valueobject.Money;

/**
 * Use case for updating an existing ingredient.
 * This use case is completely framework-agnostic and uses the TransactionManager
 * abstraction to manage transaction boundaries without depending on Spring.
 */
public class UpdateIngredientUseCase {
  private final IngredientRepository ingredientRepository;
  private final DomainEventPublisher eventPublisher;
  private final TransactionManager transactionManager;

  public UpdateIngredientUseCase(
      IngredientRepository ingredientRepository,
      DomainEventPublisher eventPublisher,
      TransactionManager transactionManager) {
    this.ingredientRepository = ingredientRepository;
    this.eventPublisher = eventPublisher;
    this.transactionManager = transactionManager;
  }

  /**
   * Executes the ingredient update within a transaction boundary.
   * The transaction is managed by the TransactionManager abstraction,
   * keeping this use case independent of any specific framework.
   *
   * @param ingredientId The ID of the ingredient to update
   * @param command The update command with new values
   * @return The updated ingredient DTO
   */
  public IngredientDto execute(Id ingredientId, UpdateIngredientCommand command) {
    return transactionManager.executeInTransaction(() -> {
      // Load existing entity
      Ingredient ingredient = ingredientRepository.findById(ingredientId)
          .orElseThrow(() -> new IllegalArgumentException("Ingredient not found with id: " + ingredientId));

      // Update entity (this will emit domain events)
      ingredient.update(
          command.name() != null ? command.name() : ingredient.getName(),
          command.packageQuantity() != null ? command.packageQuantity() : ingredient.getPackageQuantity(),
          command.packagePrice() != null ? Money.of(command.packagePrice()) : ingredient.getPackagePrice(),
          command.packageUnit() != null ? command.packageUnit() : ingredient.getPackageUnit()
      );

      // Save updated entity (within transaction)
      Ingredient savedIngredient = ingredientRepository.save(ingredient);

      // Publish domain events
      // Events will be processed by @TransactionalEventListener AFTER commit
      publishDomainEvents(savedIngredient);

      return IngredientDto.from(savedIngredient);
    });
  }

  /**
   * Publishes all domain events from the entity and clears the event list.
   * This method ensures events are published exactly once and the entity
   * is ready for subsequent operations.
   *
   * @param ingredient The ingredient entity with domain events to publish
   */
  private void publishDomainEvents(Ingredient ingredient) {
    for (DomainEvent event : ingredient.getDomainEvents()) {
      eventPublisher.publish(event);
    }
    ingredient.clearDomainEvents();
  }
}