package dto

import (
	"fmt"

	"github.com/vini/costify-go/internal/domain/valueobject"
)

// RecipeIngredientRequest represents a recipe ingredient in HTTP requests
type RecipeIngredientRequest struct {
	IngredientID string  `json:"ingredientId" binding:"required" validate:"required"`
	Quantity     float64 `json:"quantity" binding:"required,gt=0" validate:"required,gt=0"`
	Unit         string  `json:"unit" binding:"required" validate:"required"`
}

// ToRecipeIngredient converts to domain value object
func (r *RecipeIngredientRequest) ToRecipeIngredient() (valueobject.RecipeIngredient, error) {
	id := valueobject.Id{}.Of(r.IngredientID)

	unit, exists := valueobject.FromString(r.Unit)
	if !exists {
		return valueobject.RecipeIngredient{}, fmt.Errorf("invalid unit: %s", r.Unit)
	}

	return valueobject.NewRecipeIngredient(id, r.Quantity, unit)
}

// RecipeRegisterRequest represents the HTTP request for registering a recipe
type RecipeRegisterRequest struct {
	Name        string                    `json:"name" binding:"required" validate:"required"`
	Ingredients []RecipeIngredientRequest `json:"ingredients" binding:"required,min=1" validate:"required,min=1"`
}

// ToRecipeIngredients converts all ingredients to domain value objects
func (r *RecipeRegisterRequest) ToRecipeIngredients() ([]valueobject.RecipeIngredient, error) {
	var ingredients []valueobject.RecipeIngredient

	for _, ingredientReq := range r.Ingredients {
		ingredient, err := ingredientReq.ToRecipeIngredient()
		if err != nil {
			return nil, err
		}
		ingredients = append(ingredients, ingredient)
	}

	return ingredients, nil
}
