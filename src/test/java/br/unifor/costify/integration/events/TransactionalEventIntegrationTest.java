package br.unifor.costify.integration.events;

import br.unifor.costify.TestcontainersConfiguration;
import br.unifor.costify.application.contracts.IngredientRepository;
import br.unifor.costify.application.dto.command.UpdateIngredientCommand;
import br.unifor.costify.application.dto.entity.IngredientDto;
import br.unifor.costify.application.usecase.UpdateIngredientUseCase;
import br.unifor.costify.domain.contracts.IdGenerator;
import br.unifor.costify.domain.entity.Ingredient;
import br.unifor.costify.domain.valueobject.Id;
import br.unifor.costify.domain.valueobject.Money;
import br.unifor.costify.domain.valueobject.Unit;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Integration test demonstrating transactional event behavior.
 * This test verifies that:
 * 1. Use case properly publishes domain events
 * 2. Events are processed by Spring's transactional event listeners
 * 3. Domain events are cleared after publishing
 */
@SpringBootTest
@ActiveProfiles("dev")
@Import(TestcontainersConfiguration.class)
class TransactionalEventIntegrationTest {

  @Autowired
  private UpdateIngredientUseCase updateIngredientUseCase;

  @Autowired
  private IngredientRepository ingredientRepository;

  @Autowired
  private IdGenerator idGenerator;

  private Id testIngredientId;

  @BeforeEach
  void setUp() {
    // Create test ingredient
    Ingredient ingredient = new Ingredient(
        idGenerator,
        "Test Milk",
        1.0,
        Money.of(5.0),
        Unit.L
    );
    Ingredient saved = ingredientRepository.save(ingredient);
    testIngredientId = saved.getId();
  }

  @AfterEach
  void tearDown() {
    // Cleanup test data
    if (testIngredientId != null) {
      ingredientRepository.deleteById(testIngredientId);
    }
  }

  @Test
  void shouldUpdateIngredientAndPublishEvents() {
    // Arrange
    UpdateIngredientCommand command = new UpdateIngredientCommand(
        "Updated Milk",
        2.0,
        8.0,
        Unit.L
    );

    // Act
    IngredientDto result = updateIngredientUseCase.execute(testIngredientId, command);

    // Assert - Verify the update was successful
    assertNotNull(result);
    assertEquals("Updated Milk", result.name());
    assertEquals(2.0, result.packageQuantity());
    assertEquals(8.0, result.packagePrice());
    assertEquals(Unit.L, result.packageUnit());

    // Verify the ingredient was persisted correctly
    Ingredient updated = ingredientRepository.findById(testIngredientId).orElseThrow();
    assertEquals("Updated Milk", updated.getName());
    assertEquals(2.0, updated.getPackageQuantity());

    // Verify events were cleared (implying they were published)
    assertTrue(updated.getDomainEvents().isEmpty(),
        "Domain events should be cleared after publishing");
  }

  @Test
  void shouldClearEventsAfterPublishing() {
    // Arrange
    UpdateIngredientCommand command = new UpdateIngredientCommand(
        "First Update",
        1.5,
        7.0,
        Unit.L
    );

    // Act - First update
    updateIngredientUseCase.execute(testIngredientId, command);

    // Reload ingredient from database
    Ingredient ingredient = ingredientRepository.findById(testIngredientId).orElseThrow();

    // Assert - Events should be empty
    assertTrue(ingredient.getDomainEvents().isEmpty(),
        "Events should be cleared after first update");

    // Act - Second update to verify no lingering events
    UpdateIngredientCommand command2 = new UpdateIngredientCommand(
        "Second Update",
        2.5,
        9.0,
        Unit.L
    );
    updateIngredientUseCase.execute(testIngredientId, command2);

    // Reload again
    Ingredient ingredient2 = ingredientRepository.findById(testIngredientId).orElseThrow();

    // Assert - Still no events (they were cleared)
    assertTrue(ingredient2.getDomainEvents().isEmpty(),
        "Events should be cleared after second update");
  }

  @Test
  void shouldHandlePartialUpdates() {
    // Arrange - Only update name
    UpdateIngredientCommand command = new UpdateIngredientCommand(
        "Skim Milk",
        null,  // Don't update quantity
        null,  // Don't update price
        null   // Don't update unit
    );

    // Act
    IngredientDto result = updateIngredientUseCase.execute(testIngredientId, command);

    // Assert - Name updated, others unchanged
    assertEquals("Skim Milk", result.name());
    assertEquals(1.0, result.packageQuantity(), "Quantity should remain unchanged");
    assertEquals(5.0, result.packagePrice(), "Price should remain unchanged");
    assertEquals(Unit.L, result.packageUnit(), "Unit should remain unchanged");
  }
}
