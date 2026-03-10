package br.unifor.costify.application.service;

import br.unifor.costify.application.contracts.IngredientRepository;
import br.unifor.costify.application.errors.IngredientNotFoundException;
import br.unifor.costify.domain.entity.Ingredient;
import br.unifor.costify.domain.valueobject.Id;
import br.unifor.costify.domain.valueobject.RecipeIngredient;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.springframework.stereotype.Service;

@Service
public class IngredientLoaderService {
  private final IngredientRepository ingredientRepository;

  public IngredientLoaderService(IngredientRepository ingredientRepository) {
    this.ingredientRepository = ingredientRepository;
  }

  public Map<Id, Ingredient> loadIngredients(List<RecipeIngredient> recipeIngredients) {
    Map<Id, Ingredient> ingredientMap = new HashMap<>();
    
    for (RecipeIngredient recipeIngredient : recipeIngredients) {
      Id ingredientId = recipeIngredient.getIngredientId();
      
      Optional<Ingredient> ingredientOpt = ingredientRepository.findById(ingredientId);
      if (ingredientOpt.isEmpty()) {
        throw new IngredientNotFoundException("Ingredient not found with ID: " + ingredientId);
      }
      
      ingredientMap.put(ingredientId, ingredientOpt.get());
    }
    
    return ingredientMap;
  }
}