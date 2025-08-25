package br.unifor.costify.application.usecase;

import br.unifor.costify.application.contracts.IngredientRepository;
import br.unifor.costify.application.dto.command.RegisterIngredientCommand;
import br.unifor.costify.application.dto.entity.IngredientDto;
import br.unifor.costify.application.errors.IngredientAlreadyExistsException;
import br.unifor.costify.application.factory.IngredientFactory;
import br.unifor.costify.domain.entity.Ingredient;
import org.springframework.stereotype.Service;

@Service
public class RegisterIngredientUseCase {
  private final IngredientRepository ingredientRepository;
  private final IngredientFactory ingredientFactory;

  public RegisterIngredientUseCase(
      IngredientRepository ingredientRepository, IngredientFactory ingredientFactory) {
    this.ingredientRepository = ingredientRepository;
    this.ingredientFactory = ingredientFactory;
  }

  public IngredientDto execute(RegisterIngredientCommand command) {
    validateIngredientDoesNotExist(command.name());

    Ingredient ingredient =
        ingredientFactory.create(
            command.name(),
            command.packageQuantity(),
            command.packagePrice(),
            command.packageUnit());

    Ingredient savedIngredient = ingredientRepository.save(ingredient);

    return IngredientDto.from(savedIngredient);
  }

  private void validateIngredientDoesNotExist(String name) {
    if (ingredientRepository.existsByName(name)) {
      throw new IngredientAlreadyExistsException(
          "Ingredient with name '" + name + "' already exists");
    }
  }
}
