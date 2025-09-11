package entity

import (
	"github.com/vini/costify-go/internal/domain/contract"
	"github.com/vini/costify-go/internal/domain/errors"
	"github.com/vini/costify-go/internal/domain/valueobject"
)

// Recipe represents a recipe domain entity
type Recipe struct {
	id          valueobject.Id
	name        string
	ingredients []valueobject.RecipeIngredient
	totalCost   valueobject.Money
}

// NewRecipe creates a new Recipe with explicit ID
func NewRecipe(
	id valueobject.Id,
	name string,
	ingredients []valueobject.RecipeIngredient,
	totalCost valueobject.Money,
) (*Recipe, error) {
	if err := validateRecipe(name, ingredients); err != nil {
		return nil, err
	}

	// Create a copy of ingredients slice to avoid external mutations
	ingredientsCopy := make([]valueobject.RecipeIngredient, len(ingredients))
	copy(ingredientsCopy, ingredients)

	return &Recipe{
		id:          id,
		name:        name,
		ingredients: ingredientsCopy,
		totalCost:   totalCost,
	}, nil
}

// NewRecipeWithGenerator creates a new Recipe with generated ID
func NewRecipeWithGenerator(
	idGenerator contract.IdGenerator,
	name string,
	ingredients []valueobject.RecipeIngredient,
	totalCost valueobject.Money,
) (*Recipe, error) {
	if err := validateRecipe(name, ingredients); err != nil {
		return nil, err
	}

	id := valueobject.Id{}.Generate(idGenerator)

	// Create a copy of ingredients slice to avoid external mutations
	ingredientsCopy := make([]valueobject.RecipeIngredient, len(ingredients))
	copy(ingredientsCopy, ingredients)

	return &Recipe{
		id:          id,
		name:        name,
		ingredients: ingredientsCopy,
		totalCost:   totalCost,
	}, nil
}

// AddIngredient adds an ingredient to the recipe
func (r *Recipe) AddIngredient(ingredient valueobject.RecipeIngredient) {
	r.ingredients = append(r.ingredients, ingredient)
}

// RemoveIngredient removes an ingredient from the recipe by ingredient ID
func (r *Recipe) RemoveIngredient(ingredientId valueobject.Id) error {
	var updatedIngredients []valueobject.RecipeIngredient

	for _, ingredient := range r.ingredients {
		if !ingredient.IngredientId().Equals(ingredientId) {
			updatedIngredients = append(updatedIngredients, ingredient)
		}
	}

	if len(updatedIngredients) == 0 {
		return errors.NewEmptyRecipeError("Recipe must have at least one ingredient")
	}

	r.ingredients = updatedIngredients
	return nil
}

// Getters
func (r *Recipe) Id() valueobject.Id {
	return r.id
}

func (r *Recipe) Name() string {
	return r.name
}

func (r *Recipe) Ingredients() []valueobject.RecipeIngredient {
	// Return a copy to prevent external mutations
	ingredients := make([]valueobject.RecipeIngredient, len(r.ingredients))
	copy(ingredients, r.ingredients)
	return ingredients
}

func (r *Recipe) TotalCost() valueobject.Money {
	return r.totalCost
}

// validateRecipe validates recipe creation parameters
func validateRecipe(name string, ingredients []valueobject.RecipeIngredient) error {
	if len(ingredients) == 0 {
		return errors.NewEmptyRecipeError("Recipe must have at least one ingredient")
	}
	return nil
}
