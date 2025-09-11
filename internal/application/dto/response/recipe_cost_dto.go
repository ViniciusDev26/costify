package response

import (
	"github.com/vini/costify-go/internal/domain/valueobject"
)

// RecipeCostDto represents the response DTO for recipe cost breakdown
type RecipeCostDto struct {
	RecipeID        string              `json:"recipeId"`
	RecipeName      string              `json:"recipeName"`
	IngredientCosts []IngredientCostDto `json:"ingredientCosts"`
	TotalCost       float64             `json:"totalCost"`
}

// FromRecipeCost creates a RecipeCostDto from a domain value object
func FromRecipeCost(recipeCost valueobject.RecipeCost) RecipeCostDto {
	var ingredientCostDtos []IngredientCostDto
	
	for _, ingredientCost := range recipeCost.IngredientCosts() {
		ingredientCostDtos = append(ingredientCostDtos, FromIngredientCost(ingredientCost))
	}

	return RecipeCostDto{
		RecipeID:        recipeCost.RecipeId().Value(),
		RecipeName:      recipeCost.RecipeName(),
		IngredientCosts: ingredientCostDtos,
		TotalCost:       recipeCost.TotalCost().Amount(),
	}
}