package br.unifor.costify.infra.controllers.dto;

import br.unifor.costify.domain.valueobject.Unit;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

/**
 * DTO representing an ingredient within a recipe registration request.
 */
public record RecipeControllerRegisterIngredientDto(
    @NotBlank(message = "Ingredient ID is required and cannot be blank")
    @Pattern(
        regexp = "^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}$",
        message = "Ingredient ID must be a valid UUID format"
    )
    String ingredientId,
    
    @DecimalMin(value = "0.01", message = "Ingredient quantity must be greater than 0")
    double quantity,
    
    @NotNull(message = "Ingredient unit is required")
    Unit unit) {}