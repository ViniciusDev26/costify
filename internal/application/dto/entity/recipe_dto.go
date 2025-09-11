package entity

import (
	"github.com/vini/costify-go/internal/domain/entity"
)

// RecipeDto represents the data transfer object for a recipe
type RecipeDto struct {
	ID          string                `json:"id"`
	Name        string                `json:"name"`
	Ingredients []RecipeIngredientDto `json:"ingredients"`
	TotalCost   float64               `json:"totalCost"`
}

// FromRecipe creates a RecipeDto from a domain entity
func FromRecipe(recipe *entity.Recipe) RecipeDto {
	return RecipeDto{
		ID:          recipe.Id().Value(),
		Name:        recipe.Name(),
		Ingredients: FromRecipeIngredients(recipe.Ingredients()),
		TotalCost:   recipe.TotalCost().Amount(),
	}
}
