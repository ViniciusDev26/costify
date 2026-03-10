package br.unifor.costify.infra.config;

import br.unifor.costify.application.contracts.IngredientRepository;
import br.unifor.costify.application.contracts.TransactionManager;
import br.unifor.costify.application.usecase.UpdateIngredientUseCase;
import br.unifor.costify.domain.events.DomainEventPublisher;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Spring configuration for application layer use cases.
 * This configuration is in the infrastructure layer because it's responsible
 * for wiring Spring beans, but the use cases themselves remain framework-agnostic.
 */
@Configuration
public class UseCaseConfiguration {

  /**
   * Creates the UpdateIngredientUseCase bean with all required dependencies.
   * The use case itself has no Spring dependencies.
   */
  @Bean
  public UpdateIngredientUseCase updateIngredientUseCase(
      IngredientRepository ingredientRepository,
      DomainEventPublisher eventPublisher,
      TransactionManager transactionManager) {
    return new UpdateIngredientUseCase(ingredientRepository, eventPublisher, transactionManager);
  }
}
