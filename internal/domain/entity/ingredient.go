package entity

import (
	"strings"

	"github.com/vini/costify-go/internal/domain/contract"
	"github.com/vini/costify-go/internal/domain/errors"
	"github.com/vini/costify-go/internal/domain/valueobject"
)

// Ingredient represents an ingredient domain entity
type Ingredient struct {
	id              valueobject.Id
	name            string
	packageQuantity float64
	packagePrice    valueobject.Money
	packageUnit     valueobject.Unit
}

// NewIngredient creates a new Ingredient with explicit ID
func NewIngredient(
	id valueobject.Id,
	name string,
	packageQuantity float64,
	packagePrice valueobject.Money,
	packageUnit valueobject.Unit,
) (*Ingredient, error) {
	if err := validateIngredient(name, packageQuantity); err != nil {
		return nil, err
	}

	return &Ingredient{
		id:              id,
		name:            name,
		packageQuantity: packageQuantity,
		packagePrice:    packagePrice,
		packageUnit:     packageUnit,
	}, nil
}

// NewIngredientWithGenerator creates a new Ingredient with generated ID
func NewIngredientWithGenerator(
	idGenerator contract.IdGenerator,
	name string,
	packageQuantity float64,
	packagePrice valueobject.Money,
	packageUnit valueobject.Unit,
) (*Ingredient, error) {
	if err := validateIngredient(name, packageQuantity); err != nil {
		return nil, err
	}

	id := valueobject.Id{}.Generate(idGenerator)

	return &Ingredient{
		id:              id,
		name:            name,
		packageQuantity: packageQuantity,
		packagePrice:    packagePrice,
		packageUnit:     packageUnit,
	}, nil
}

// UnitCost calculates the cost per base unit of the ingredient
func (i *Ingredient) UnitCost() float64 {
	baseQuantity := i.packageUnit.ToBase(i.packageQuantity)
	return i.packagePrice.Amount() / baseQuantity
}

// Getters
func (i *Ingredient) Id() valueobject.Id {
	return i.id
}

func (i *Ingredient) Name() string {
	return i.name
}

func (i *Ingredient) PackageQuantity() float64 {
	return i.packageQuantity
}

func (i *Ingredient) PackagePrice() valueobject.Money {
	return i.packagePrice
}

func (i *Ingredient) PackageUnit() valueobject.Unit {
	return i.packageUnit
}

// validateIngredient validates ingredient creation parameters
func validateIngredient(name string, packageQuantity float64) error {
	if strings.TrimSpace(name) == "" {
		return errors.NewInvalidIngredientNameError("Ingredient name cannot be null or empty")
	}
	if packageQuantity <= 0 {
		return errors.NewInvalidQuantityError("Package quantity must be greater than zero")
	}
	return nil
}