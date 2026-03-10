package br.unifor.costify.infra.controllers.dto;

import br.unifor.costify.domain.valueobject.Unit;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * Request DTO for ingredient registration with comprehensive validation.
 */
public record IngredientControllerRegisterRequest(
    @NotBlank(message = "Ingredient name is required and cannot be blank")
    @Size(min = 2, max = 100, message = "Ingredient name must be between 2 and 100 characters")
    String name,
    
    @DecimalMin(value = "0.01", message = "Package quantity must be greater than 0")
    double packageQuantity,
    
    @DecimalMin(value = "0.01", message = "Package price must be greater than 0")
    double packagePrice,
    
    @NotNull(message = "Package unit is required")
    Unit packageUnit) {}
