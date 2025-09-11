package response

import (
	"github.com/vini/costify-go/internal/domain/valueobject"
)

// IngredientCostDto represents the response DTO for ingredient cost breakdown
type IngredientCostDto struct {
	IngredientID   string  `json:"ingredientId"`
	IngredientName string  `json:"ingredientName"`
	QuantityUsed   float64 `json:"quantityUsed"`
	Unit           string  `json:"unit"`
	Cost           float64 `json:"cost"`
}

// FromIngredientCost creates an IngredientCostDto from a domain value object
func FromIngredientCost(ingredientCost valueobject.IngredientCost) IngredientCostDto {
	return IngredientCostDto{
		IngredientID:   ingredientCost.IngredientId().Value(),
		IngredientName: ingredientCost.IngredientName(),
		QuantityUsed:   ingredientCost.QuantityUsed(),
		Unit:           ingredientCost.Unit().String(),
		Cost:           ingredientCost.Cost().Amount(),
	}
}
