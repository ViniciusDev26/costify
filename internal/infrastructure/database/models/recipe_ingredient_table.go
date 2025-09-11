package models

import (
	"fmt"
	"time"

	"github.com/vini/costify-go/internal/domain/valueobject"
)

// RecipeIngredientTable represents the recipe_ingredients table model for GORM
type RecipeIngredientTable struct {
	ID           uint      `gorm:"primaryKey;autoIncrement;column:id"`
	RecipeID     string    `gorm:"not null;column:recipe_id"`
	IngredientID string    `gorm:"not null;column:ingredient_id"`
	Quantity     float64   `gorm:"not null;column:quantity;check:quantity > 0"`
	Unit         string    `gorm:"not null;column:unit"`
	CreatedAt    time.Time `gorm:"default:CURRENT_TIMESTAMP;column:created_at"`
}

// TableName returns the table name for GORM
func (RecipeIngredientTable) TableName() string {
	return "recipe_ingredients"
}

// ToValueObject converts GORM model to domain value object
func (rit *RecipeIngredientTable) ToValueObject() (valueobject.RecipeIngredient, error) {
	ingredientId := valueobject.Id{}.Of(rit.IngredientID)

	unit, exists := valueobject.FromString(rit.Unit)
	if !exists {
		return valueobject.RecipeIngredient{}, fmt.Errorf("invalid unit: %s", rit.Unit)
	}

	return valueobject.NewRecipeIngredient(ingredientId, rit.Quantity, unit)
}

// FromRecipeIngredientValueObject converts domain value object to GORM model
func FromRecipeIngredientValueObject(recipeID string, ri valueobject.RecipeIngredient) *RecipeIngredientTable {
	return &RecipeIngredientTable{
		RecipeID:     recipeID,
		IngredientID: ri.IngredientId().Value(),
		Quantity:     ri.Quantity(),
		Unit:         ri.Unit().Name(),
		CreatedAt:    time.Now(),
	}
}