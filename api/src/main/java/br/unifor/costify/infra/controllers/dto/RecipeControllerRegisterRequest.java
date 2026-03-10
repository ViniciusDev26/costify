package br.unifor.costify.infra.controllers.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import java.util.List;

/**
 * Request DTO for recipe registration with comprehensive validation.
 */
public record RecipeControllerRegisterRequest(
    @NotBlank(message = "Recipe name is required and cannot be blank")
    @Size(min = 2, max = 100, message = "Recipe name must be between 2 and 100 characters")
    String name,
    
    @NotEmpty(message = "Recipe must have at least one ingredient")
    @Size(max = 50, message = "Recipe cannot have more than 50 ingredients")
    @Valid
    List<RecipeControllerRegisterIngredientDto> ingredients) {}