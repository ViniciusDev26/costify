package service

import (
	"fmt"

	"github.com/vini/costify-go/internal/domain/entity"
	"github.com/vini/costify-go/internal/domain/valueobject"
)

// RecipeCostCalculationService provides domain service for calculating recipe costs
type RecipeCostCalculationService struct{}

// NewRecipeCostCalculationService creates a new instance of the service
func NewRecipeCostCalculationService() *RecipeCostCalculationService {
	return &RecipeCostCalculationService{}
}

// CalculateCost calculates the total cost of a recipe given the ingredients map
func (s *RecipeCostCalculationService) CalculateCost(
	recipe *entity.Recipe,
	ingredientMap map[valueobject.Id]*entity.Ingredient,
) (valueobject.RecipeCost, error) {
	if err := s.validateInputs(recipe, ingredientMap); err != nil {
		return valueobject.RecipeCost{}, err
	}

	var ingredientCosts []valueobject.IngredientCost

	for _, recipeIngredient := range recipe.Ingredients() {
		ingredient, exists := ingredientMap[recipeIngredient.IngredientId()]
		if !exists {
			return valueobject.RecipeCost{}, fmt.Errorf(
				"ingredient not found with ID: %s", recipeIngredient.IngredientId())
		}

		ingredientCost, err := s.calculateIngredientCost(ingredient, recipeIngredient)
		if err != nil {
			return valueobject.RecipeCost{}, err
		}

		ingredientCosts = append(ingredientCosts, ingredientCost)
	}

	return valueobject.NewRecipeCost(recipe.Id(), recipe.Name(), ingredientCosts)
}

// validateInputs validates the input parameters
func (s *RecipeCostCalculationService) validateInputs(
	recipe *entity.Recipe,
	ingredientMap map[valueobject.Id]*entity.Ingredient,
) error {
	if recipe == nil {
		return fmt.Errorf("recipe cannot be nil")
	}
	if ingredientMap == nil {
		return fmt.Errorf("ingredient map cannot be nil")
	}
	return nil
}

// calculateIngredientCost calculates the cost for a single ingredient in the recipe
func (s *RecipeCostCalculationService) calculateIngredientCost(
	ingredient *entity.Ingredient,
	recipeIngredient valueobject.RecipeIngredient,
) (valueobject.IngredientCost, error) {
	// Use existing UnitCost() method which already handles unit conversion properly
	unitCost := ingredient.UnitCost()

	// Convert recipe quantity to base units for calculation
	recipeQuantityInBaseUnits := recipeIngredient.Unit().ToBase(recipeIngredient.Quantity())

	// Calculate total cost for the recipe quantity
	totalCostAmount := unitCost * recipeQuantityInBaseUnits
	totalCost, err := valueobject.Money{}.Of(totalCostAmount)
	if err != nil {
		return valueobject.IngredientCost{}, err
	}

	return valueobject.NewIngredientCost(
		ingredient.Id(),
		ingredient.Name(),
		recipeIngredient.Quantity(), // Keep original quantity
		recipeIngredient.Unit(),     // Keep original unit
		totalCost,
	)
}