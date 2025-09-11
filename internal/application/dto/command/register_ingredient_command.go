package command

import (
	"fmt"
	"strings"

	"github.com/vini/costify-go/internal/domain/valueobject"
)

// RegisterIngredientCommand represents the command to register a new ingredient
type RegisterIngredientCommand struct {
	Name            string              `json:"name" validate:"required"`
	PackageQuantity float64             `json:"packageQuantity" validate:"required,gt=0"`
	PackagePrice    float64             `json:"packagePrice" validate:"required,gte=0"`
	PackageUnit     valueobject.Unit    `json:"packageUnit" validate:"required"`
}

// NewRegisterIngredientCommand creates a new RegisterIngredientCommand with validation
func NewRegisterIngredientCommand(
	name string,
	packageQuantity float64,
	packagePrice float64,
	packageUnit valueobject.Unit,
) (RegisterIngredientCommand, error) {
	if strings.TrimSpace(name) == "" {
		return RegisterIngredientCommand{}, fmt.Errorf("ingredient name cannot be null or empty")
	}
	if packageQuantity <= 0 {
		return RegisterIngredientCommand{}, fmt.Errorf("package quantity must be greater than zero")
	}
	if packagePrice < 0 {
		return RegisterIngredientCommand{}, fmt.Errorf("package price cannot be negative")
	}

	return RegisterIngredientCommand{
		Name:            name,
		PackageQuantity: packageQuantity,
		PackagePrice:    packagePrice,
		PackageUnit:     packageUnit,
	}, nil
}