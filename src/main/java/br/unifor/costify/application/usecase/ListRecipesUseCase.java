package br.unifor.costify.application.usecase;

import br.unifor.costify.application.contracts.RecipeRepository;
import br.unifor.costify.application.dto.entity.RecipeDto;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class ListRecipesUseCase {
  private final RecipeRepository recipeRepository;

  public ListRecipesUseCase(RecipeRepository recipeRepository) {
    this.recipeRepository = recipeRepository;
  }

  public List<RecipeDto> execute() {
    return recipeRepository.findAll().stream().map(RecipeDto::from).toList();
  }
}
