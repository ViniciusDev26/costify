package br.unifor.costify.application.usecase;

import br.unifor.costify.application.contracts.IngredientRepository;
import br.unifor.costify.application.contracts.TransactionManager;
import br.unifor.costify.application.contracts.TransactionalOperation;
import br.unifor.costify.application.dto.command.UpdateIngredientCommand;
import br.unifor.costify.application.dto.entity.IngredientDto;
import br.unifor.costify.domain.entity.Ingredient;
import br.unifor.costify.domain.events.DomainEvent;
import br.unifor.costify.domain.events.DomainEventPublisher;
import br.unifor.costify.domain.events.ingredient.IngredientUpdatedEvent;
import br.unifor.costify.domain.valueobject.Id;
import br.unifor.costify.domain.valueobject.Money;
import br.unifor.costify.domain.valueobject.Unit;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class UpdateIngredientUseCaseTest {

  private IngredientRepository ingredientRepository;
  private DomainEventPublisher eventPublisher;
  private TransactionManager transactionManager;
  private UpdateIngredientUseCase updateIngredientUseCase;

  @BeforeEach
  void setUp() {
    ingredientRepository = mock(IngredientRepository.class);
    eventPublisher = mock(DomainEventPublisher.class);
    transactionManager = mock(TransactionManager.class);

    // Configure the mock transaction manager to execute the operation directly
    // This mimics the real behavior: RuntimeExceptions pass through, checked exceptions are wrapped
    when(transactionManager.executeInTransaction(any())).thenAnswer(invocation -> {
      TransactionalOperation<?> operation = invocation.getArgument(0);
      try {
        return operation.execute();
      } catch (RuntimeException e) {
        // Pass through runtime exceptions as-is
        throw e;
      } catch (Exception e) {
        // Wrap checked exceptions
        throw new RuntimeException("Transaction operation failed", e);
      }
    });

    updateIngredientUseCase = new UpdateIngredientUseCase(
        ingredientRepository,
        eventPublisher,
        transactionManager
    );
  }

  @Test
  void shouldUpdateIngredientSuccessfully() {
    // Arrange
    Id ingredientId = Id.of("test-id");
    Ingredient existingIngredient = new Ingredient(
        ingredientId,
        "Milk",
        1.0,
        Money.of(5.0),
        Unit.L
    );

    when(ingredientRepository.findById(ingredientId))
        .thenReturn(Optional.of(existingIngredient));
    when(ingredientRepository.save(any(Ingredient.class)))
        .thenAnswer(invocation -> invocation.getArgument(0));

    UpdateIngredientCommand command = new UpdateIngredientCommand(
        "Skim Milk",
        2.0,
        8.0,
        Unit.L
    );

    // Act
    IngredientDto result = updateIngredientUseCase.execute(ingredientId, command);

    // Assert
    assertNotNull(result);
    assertEquals("Skim Milk", result.name());
    assertEquals(2.0, result.packageQuantity());
    assertEquals(8.0, result.packagePrice());
    assertEquals(Unit.L, result.packageUnit());

    verify(ingredientRepository).findById(ingredientId);
    verify(ingredientRepository).save(any(Ingredient.class));
  }

  @Test
  void shouldPublishIngredientUpdatedEvent() {
    // Arrange
    Id ingredientId = Id.of("test-id");
    Ingredient existingIngredient = new Ingredient(
        ingredientId,
        "Milk",
        1.0,
        Money.of(5.0),
        Unit.L
    );

    ArgumentCaptor<DomainEvent> eventCaptor = ArgumentCaptor.forClass(DomainEvent.class);
    when(ingredientRepository.findById(ingredientId))
        .thenReturn(Optional.of(existingIngredient));
    when(ingredientRepository.save(any(Ingredient.class)))
        .thenAnswer(invocation -> invocation.getArgument(0));

    UpdateIngredientCommand command = new UpdateIngredientCommand(
        "Skim Milk",
        2.0,
        8.0,
        Unit.L
    );

    // Act
    updateIngredientUseCase.execute(ingredientId, command);

    // Assert - Verify event was published
    verify(eventPublisher, times(1)).publish(eventCaptor.capture());
    DomainEvent publishedEvent = eventCaptor.getValue();

    assertTrue(publishedEvent instanceof IngredientUpdatedEvent);
    IngredientUpdatedEvent event = (IngredientUpdatedEvent) publishedEvent;
    assertEquals(ingredientId, event.getIngredientId());
    assertEquals("Skim Milk", event.getNewName());
    assertEquals(2.0, event.getNewPackageQuantity());
    assertEquals(Money.of(8.0), event.getNewPackagePrice());
    assertEquals(Unit.L, event.getNewPackageUnit());
  }

  @Test
  void shouldClearDomainEventsAfterSave() {
    // Arrange
    Id ingredientId = Id.of("test-id");
    Ingredient existingIngredient = new Ingredient(
        ingredientId,
        "Milk",
        1.0,
        Money.of(5.0),
        Unit.L
    );

    ArgumentCaptor<Ingredient> ingredientCaptor = ArgumentCaptor.forClass(Ingredient.class);
    when(ingredientRepository.findById(ingredientId))
        .thenReturn(Optional.of(existingIngredient));
    when(ingredientRepository.save(any(Ingredient.class)))
        .thenAnswer(invocation -> invocation.getArgument(0));

    UpdateIngredientCommand command = new UpdateIngredientCommand(
        "Skim Milk",
        2.0,
        8.0,
        Unit.L
    );

    // Act
    updateIngredientUseCase.execute(ingredientId, command);

    // Assert - Events should be cleared after processing
    verify(ingredientRepository).save(ingredientCaptor.capture());
    Ingredient savedIngredient = ingredientCaptor.getValue();

    // The use case should clear events after processing them
    assertTrue(savedIngredient.getDomainEvents().isEmpty());
  }

  @Test
  void shouldThrowExceptionWhenIngredientNotFound() {
    // Arrange
    Id ingredientId = Id.of("non-existent-id");
    when(ingredientRepository.findById(ingredientId))
        .thenReturn(Optional.empty());

    UpdateIngredientCommand command = new UpdateIngredientCommand(
        "Skim Milk",
        2.0,
        8.0,
        Unit.L
    );

    // Act & Assert
    assertThrows(IllegalArgumentException.class, () -> {
      updateIngredientUseCase.execute(ingredientId, command);
    });

    verify(ingredientRepository).findById(ingredientId);
    verify(ingredientRepository, never()).save(any(Ingredient.class));
  }

  @Test
  void shouldUpdateOnlyProvidedFields() {
    // Arrange
    Id ingredientId = Id.of("test-id");
    Ingredient existingIngredient = new Ingredient(
        ingredientId,
        "Milk",
        1.0,
        Money.of(5.0),
        Unit.L
    );

    when(ingredientRepository.findById(ingredientId))
        .thenReturn(Optional.of(existingIngredient));
    when(ingredientRepository.save(any(Ingredient.class)))
        .thenAnswer(invocation -> invocation.getArgument(0));

    // Only update name
    UpdateIngredientCommand command = new UpdateIngredientCommand(
        "Skim Milk",
        null,
        null,
        null
    );

    // Act
    IngredientDto result = updateIngredientUseCase.execute(ingredientId, command);

    // Assert
    assertEquals("Skim Milk", result.name());
    assertEquals(1.0, result.packageQuantity()); // Should keep old value
    assertEquals(5.0, result.packagePrice()); // Should keep old value
    assertEquals(Unit.L, result.packageUnit()); // Should keep old value
  }
}
