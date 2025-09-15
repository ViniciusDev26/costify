package valueobject

import (
	"fmt"
	"strings"

	"github.com/vini/costify-go/internal/domain/errors"
)

// RecipeCost represents the complete cost breakdown for a recipe
type RecipeCost struct {
	recipeId        Id
	recipeName      string
	ingredientCosts []IngredientCost
	totalCost       Money
}

// NewRecipeCost creates a new RecipeCost with validation and calculates total cost
func NewRecipeCost(
	recipeId Id,
	recipeName string,
	ingredientCosts []IngredientCost,
) (RecipeCost, error) {
	if strings.TrimSpace(recipeName) == "" {
		return RecipeCost{}, errors.NewInvalidIngredientNameError("Recipe name cannot be null or empty")
	}
	if len(ingredientCosts) == 0 {
		return RecipeCost{}, errors.NewEmptyRecipeError("Recipe must have at least one ingredient cost")
	}

	// Calculate total cost by summing all ingredient costs
	totalCost := ZeroMoney()
	for _, ingredientCost := range ingredientCosts {
		totalCost = totalCost.Add(ingredientCost.Cost())
	}

	// Create a copy of ingredientCosts to avoid external mutations
	costsCopy := make([]IngredientCost, len(ingredientCosts))
	copy(costsCopy, ingredientCosts)

	return RecipeCost{
		recipeId:        recipeId,
		recipeName:      recipeName,
		ingredientCosts: costsCopy,
		totalCost:       totalCost,
	}, nil
}

// Getters
func (rc RecipeCost) RecipeId() Id {
	return rc.recipeId
}

func (rc RecipeCost) RecipeName() string {
	return rc.recipeName
}

func (rc RecipeCost) IngredientCosts() []IngredientCost {
	// Return a copy to prevent external mutations
	costs := make([]IngredientCost, len(rc.ingredientCosts))
	copy(costs, rc.ingredientCosts)
	return costs
}

func (rc RecipeCost) TotalCost() Money {
	return rc.totalCost
}

// Equals checks if two RecipeCosts are equal
func (rc RecipeCost) Equals(other RecipeCost) bool {
	if !rc.recipeId.Equals(other.recipeId) ||
		rc.recipeName != other.recipeName ||
		!rc.totalCost.Equals(other.totalCost) ||
		len(rc.ingredientCosts) != len(other.ingredientCosts) {
		return false
	}

	for i, cost := range rc.ingredientCosts {
		if !cost.Equals(other.ingredientCosts[i]) {
			return false
		}
	}

	return true
}

// String implements the Stringer interface
func (rc RecipeCost) String() string {
	return fmt.Sprintf("RecipeCost{recipeId=%s, recipeName='%s', totalCost=%s, ingredientCount=%d}",
		rc.recipeId, rc.recipeName, rc.totalCost, len(rc.ingredientCosts))
}
