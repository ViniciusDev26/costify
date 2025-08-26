package br.unifor.costify.application.usecase;

import br.unifor.costify.application.contracts.IngredientRepository;
import br.unifor.costify.application.dto.command.UpdateIngredientCommand;
import br.unifor.costify.application.dto.entity.IngredientDto;
import br.unifor.costify.domain.entity.Ingredient;
import br.unifor.costify.domain.valueobject.Id;
import org.springframework.stereotype.Service;

@Service
public class UpdateIngredientUseCase {
  private final IngredientRepository ingredientRepository;

  public UpdateIngredientUseCase(IngredientRepository ingredientRepository) {
    this.ingredientRepository = ingredientRepository;
  }

  public IngredientDto execute(Id ingredientId, UpdateIngredientCommand command) {
    // Load existing immutable entity
    Ingredient existing = ingredientRepository.findById(ingredientId)
        .orElseThrow(() -> new IllegalArgumentException("Ingredient not found with id: " + ingredientId));

    // Create new immutable entity with updated values
    Ingredient updated = new Ingredient(
        existing.getId(), // Keep same ID
        command.name() != null ? command.name() : existing.getName(),
        command.packageQuantity() != null ? command.packageQuantity() : existing.getPackageQuantity(),
        command.packagePrice() != null ? command.packagePrice() : existing.getPackagePrice(),
        command.packageUnit() != null ? command.packageUnit() : existing.getPackageUnit()
    );

    // Replace old entity with new one
    Ingredient savedIngredient = ingredientRepository.save(updated);

    return IngredientDto.from(savedIngredient);
  }
}