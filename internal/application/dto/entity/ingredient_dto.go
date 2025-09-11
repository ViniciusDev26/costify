package entity

import (
	"github.com/vini/costify-go/internal/domain/entity"
	"github.com/vini/costify-go/internal/domain/valueobject"
)

// IngredientDto represents the data transfer object for an ingredient
type IngredientDto struct {
	ID              string              `json:"id"`
	Name            string              `json:"name"`
	PackageQuantity float64             `json:"packageQuantity"`
	PackagePrice    float64             `json:"packagePrice"`
	PackageUnit     valueobject.Unit    `json:"packageUnit"`
	UnitCost        float64             `json:"unitCost"`
}

// FromIngredient creates an IngredientDto from a domain entity
func FromIngredient(ingredient *entity.Ingredient) IngredientDto {
	return IngredientDto{
		ID:              ingredient.Id().Value(),
		Name:            ingredient.Name(),
		PackageQuantity: ingredient.PackageQuantity(),
		PackagePrice:    ingredient.PackagePrice().Amount(),
		PackageUnit:     ingredient.PackageUnit(),
		UnitCost:        ingredient.UnitCost(),
	}
}