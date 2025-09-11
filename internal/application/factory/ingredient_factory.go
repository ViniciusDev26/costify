package factory

import (
	"github.com/vini/costify-go/internal/application/errors"
	"github.com/vini/costify-go/internal/domain/contract"
	"github.com/vini/costify-go/internal/domain/entity"
	"github.com/vini/costify-go/internal/domain/valueobject"
)

// IngredientFactory provides factory methods for creating ingredients
type IngredientFactory struct {
	idGenerator contract.IdGenerator
}

// NewIngredientFactory creates a new IngredientFactory
func NewIngredientFactory(idGenerator contract.IdGenerator) *IngredientFactory {
	return &IngredientFactory{
		idGenerator: idGenerator,
	}
}

// Create creates an ingredient with generated ID
func (f *IngredientFactory) Create(
	name string,
	packageQuantity float64,
	packagePrice float64,
	packageUnit string,
) (*entity.Ingredient, error) {
	money, err := valueobject.Money{}.Of(packagePrice)
	if err != nil {
		return nil, err
	}

	// Defensive programming: validate unit even though HTTP layer may have validated it
	// This ensures the factory remains independent and robust
	unit, exists := valueobject.FromString(packageUnit)
	if !exists {
		return nil, errors.NewInvalidUnitError("Invalid unit: " + packageUnit)
	}

	return entity.NewIngredientWithGenerator(
		f.idGenerator,
		name,
		packageQuantity,
		money,
		unit,
	)
}

// CreateWithId creates an ingredient with explicit ID
func (f *IngredientFactory) CreateWithId(
	id valueobject.Id,
	name string,
	packageQuantity float64,
	packagePrice float64,
	packageUnit valueobject.Unit,
) (*entity.Ingredient, error) {
	money, err := valueobject.Money{}.Of(packagePrice)
	if err != nil {
		return nil, err
	}

	return entity.NewIngredient(
		id,
		name,
		packageQuantity,
		money,
		packageUnit,
	)
}
