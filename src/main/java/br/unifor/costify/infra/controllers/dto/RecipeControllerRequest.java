package br.unifor.costify.infra.controllers.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import java.util.List;

public record RecipeControllerRequest(
    @NotBlank String name,
    @NotEmpty @Valid List<RecipeIngredientDto> ingredients) {}