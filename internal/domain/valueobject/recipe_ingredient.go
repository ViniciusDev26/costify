package valueobject

import (
	"github.com/vini/costify-go/internal/domain/errors"
)

// RecipeIngredient represents an ingredient used in a recipe with quantity and unit
type RecipeIngredient struct {
	ingredientId Id
	quantity     float64
	unit         Unit
}

// NewRecipeIngredient creates a new RecipeIngredient with validation
func NewRecipeIngredient(ingredientId Id, quantity float64, unit Unit) (RecipeIngredient, error) {
	if quantity <= 0 {
		return RecipeIngredient{}, errors.NewInvalidQuantityError("Quantity must be greater than zero")
	}

	return RecipeIngredient{
		ingredientId: ingredientId,
		quantity:     quantity,
		unit:         unit,
	}, nil
}

// IngredientId returns the ingredient ID
func (ri RecipeIngredient) IngredientId() Id {
	return ri.ingredientId
}

// Quantity returns the quantity
func (ri RecipeIngredient) Quantity() float64 {
	return ri.quantity
}

// Unit returns the unit
func (ri RecipeIngredient) Unit() Unit {
	return ri.unit
}

// Equals checks if two RecipeIngredients are equal
func (ri RecipeIngredient) Equals(other RecipeIngredient) bool {
	return ri.ingredientId.Equals(other.ingredientId) &&
		ri.quantity == other.quantity &&
		ri.unit == other.unit
}
