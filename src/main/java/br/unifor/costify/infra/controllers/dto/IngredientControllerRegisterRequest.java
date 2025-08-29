package br.unifor.costify.infra.controllers.dto;

import br.unifor.costify.domain.valueobject.Unit;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record IngredientControllerRegisterRequest(
    @NotBlank String name,
    @DecimalMin(value = "1") double packageQuantity,
    @DecimalMin(value = "1") double packagePrice,
    @NotNull Unit packageUnit) {}
