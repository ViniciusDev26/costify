package br.unifor.costify.application.usecase;

import br.unifor.costify.application.contracts.IngredientRepository;
import br.unifor.costify.application.dto.entity.IngredientDto;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class ListIngredientsUseCase {
  private final IngredientRepository ingredientRepository;

  public ListIngredientsUseCase(IngredientRepository ingredientRepository) {
    this.ingredientRepository = ingredientRepository;
  }

  public List<IngredientDto> execute() {
    return ingredientRepository.findAll().stream().map(IngredientDto::from).toList();
  }
}
