package br.unifor.costify.infra.controllers.dto;

import br.unifor.costify.domain.valueobject.Unit;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record RecipeControllerRegisterIngredientDto(
    @NotBlank String ingredientId,
    @DecimalMin(value = "0.01") double quantity,
    @NotNull Unit unit) {}