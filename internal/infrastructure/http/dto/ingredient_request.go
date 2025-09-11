package dto

import "github.com/vini/costify-go/internal/domain/valueobject"

// IngredientRegisterRequest represents the HTTP request for registering an ingredient
type IngredientRegisterRequest struct {
	Name            string  `json:"name" binding:"required" validate:"required"`
	PackageQuantity float64 `json:"packageQuantity" binding:"required,gt=0" validate:"required,gt=0"`
	PackagePrice    float64 `json:"packagePrice" binding:"required,gte=0" validate:"required,gte=0"`
	PackageUnit     string  `json:"packageUnit" binding:"required" validate:"required"`
}

// ToUnit converts the string unit to a valueobject.Unit
func (r *IngredientRegisterRequest) ToUnit() (valueobject.Unit, bool) {
	return valueobject.FromString(r.PackageUnit)
}