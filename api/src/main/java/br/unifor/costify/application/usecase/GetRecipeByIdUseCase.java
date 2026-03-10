package br.unifor.costify.application.usecase;

import br.unifor.costify.application.contracts.RecipeRepository;
import br.unifor.costify.application.dto.entity.RecipeDto;
import br.unifor.costify.application.errors.RecipeNotFoundException;
import br.unifor.costify.domain.valueobject.Id;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class GetRecipeByIdUseCase {

    private final RecipeRepository recipeRepository;

    public RecipeDto execute(String recipeId) {
        var id = Id.of(recipeId);
        var recipe = recipeRepository.findById(id)
                .orElseThrow(() -> RecipeNotFoundException.withId(recipeId));

        return RecipeDto.from(recipe);
    }
}
