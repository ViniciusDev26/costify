package factory

import (
	"github.com/vini/costify-go/internal/domain/contract"
	"github.com/vini/costify-go/internal/domain/entity"
	"github.com/vini/costify-go/internal/domain/valueobject"
)

// RecipeFactory provides factory methods for creating recipes
type RecipeFactory struct {
	idGenerator contract.IdGenerator
}

// NewRecipeFactory creates a new RecipeFactory
func NewRecipeFactory(idGenerator contract.IdGenerator) *RecipeFactory {
	return &RecipeFactory{
		idGenerator: idGenerator,
	}
}

// Create creates a recipe with generated ID
func (f *RecipeFactory) Create(
	name string,
	ingredients []valueobject.RecipeIngredient,
	totalCost valueobject.Money,
) (*entity.Recipe, error) {
	return entity.NewRecipeWithGenerator(
		f.idGenerator,
		name,
		ingredients,
		totalCost,
	)
}

// CreateWithId creates a recipe with explicit ID
func (f *RecipeFactory) CreateWithId(
	id valueobject.Id,
	name string,
	ingredients []valueobject.RecipeIngredient,
	totalCost valueobject.Money,
) (*entity.Recipe, error) {
	return entity.NewRecipe(
		id,
		name,
		ingredients,
		totalCost,
	)
}