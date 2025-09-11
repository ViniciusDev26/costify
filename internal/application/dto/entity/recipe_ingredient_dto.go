package entity

import "github.com/vini/costify-go/internal/domain/valueobject"

// RecipeIngredientDto represents the data transfer object for a recipe ingredient
type RecipeIngredientDto struct {
	IngredientId string  `json:"ingredientId"`
	Quantity     float64 `json:"quantity"`
	Unit         string  `json:"unit"`
}

// FromRecipeIngredient creates a RecipeIngredientDto from a domain value object
func FromRecipeIngredient(ingredient valueobject.RecipeIngredient) RecipeIngredientDto {
	return RecipeIngredientDto{
		IngredientId: ingredient.IngredientId().Value(),
		Quantity:     ingredient.Quantity(),
		Unit:         ingredient.Unit().String(),
	}
}

// FromRecipeIngredients creates a slice of RecipeIngredientDto from domain value objects
func FromRecipeIngredients(ingredients []valueobject.RecipeIngredient) []RecipeIngredientDto {
	dtos := make([]RecipeIngredientDto, len(ingredients))
	for i, ingredient := range ingredients {
		dtos[i] = FromRecipeIngredient(ingredient)
	}
	return dtos
}
