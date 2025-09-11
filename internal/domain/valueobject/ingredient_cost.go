package valueobject

import (
	"fmt"
	"strings"
)

// IngredientCost represents the cost breakdown for an ingredient in a recipe
type IngredientCost struct {
	ingredientId   Id
	ingredientName string
	quantityUsed   float64
	unit           Unit
	cost           Money
}

// NewIngredientCost creates a new IngredientCost with validation
func NewIngredientCost(
	ingredientId Id,
	ingredientName string,
	quantityUsed float64,
	unit Unit,
	cost Money,
) (IngredientCost, error) {
	if strings.TrimSpace(ingredientName) == "" {
		return IngredientCost{}, fmt.Errorf("ingredient name cannot be null or empty")
	}
	if quantityUsed <= 0 {
		return IngredientCost{}, fmt.Errorf("quantity used must be greater than zero")
	}

	return IngredientCost{
		ingredientId:   ingredientId,
		ingredientName: ingredientName,
		quantityUsed:   quantityUsed,
		unit:           unit,
		cost:           cost,
	}, nil
}

// Getters
func (ic IngredientCost) IngredientId() Id {
	return ic.ingredientId
}

func (ic IngredientCost) IngredientName() string {
	return ic.ingredientName
}

func (ic IngredientCost) QuantityUsed() float64 {
	return ic.quantityUsed
}

func (ic IngredientCost) Unit() Unit {
	return ic.unit
}

func (ic IngredientCost) Cost() Money {
	return ic.cost
}

// Equals checks if two IngredientCosts are equal
func (ic IngredientCost) Equals(other IngredientCost) bool {
	return ic.ingredientId.Equals(other.ingredientId) &&
		ic.ingredientName == other.ingredientName &&
		ic.quantityUsed == other.quantityUsed &&
		ic.unit == other.unit &&
		ic.cost.Equals(other.cost)
}

// String implements the Stringer interface
func (ic IngredientCost) String() string {
	return fmt.Sprintf("IngredientCost{ingredientId=%s, ingredientName='%s', quantityUsed=%.2f %s, cost=%s}",
		ic.ingredientId, ic.ingredientName, ic.quantityUsed, ic.unit, ic.cost)
}
