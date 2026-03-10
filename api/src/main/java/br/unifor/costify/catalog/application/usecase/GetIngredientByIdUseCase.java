package br.unifor.costify.catalog.application.usecase;

import br.unifor.costify.catalog.application.contracts.IngredientRepository;
import br.unifor.costify.catalog.application.dto.entity.IngredientDto;
import br.unifor.costify.catalog.application.errors.IngredientNotFoundException;
import br.unifor.costify.shared.domain.valueobject.Id;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class GetIngredientByIdUseCase {

    private final IngredientRepository ingredientRepository;

    public IngredientDto execute(String ingredientId) {
        var id = Id.of(ingredientId);
        var ingredient = ingredientRepository.findById(id)
                .orElseThrow(() -> IngredientNotFoundException.withId(ingredientId));

        return IngredientDto.from(ingredient);
    }
}
