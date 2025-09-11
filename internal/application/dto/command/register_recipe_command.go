package command

import (
	"fmt"
	"strings"

	"github.com/vini/costify-go/internal/domain/valueobject"
)

// RegisterRecipeCommand represents the command to register a new recipe
type RegisterRecipeCommand struct {
	Name        string                           `json:"name" validate:"required"`
	Ingredients []valueobject.RecipeIngredient   `json:"ingredients" validate:"required,min=1"`
}

// NewRegisterRecipeCommand creates a new RegisterRecipeCommand with validation
func NewRegisterRecipeCommand(
	name string,
	ingredients []valueobject.RecipeIngredient,
) (RegisterRecipeCommand, error) {
	if strings.TrimSpace(name) == "" {
		return RegisterRecipeCommand{}, fmt.Errorf("recipe name cannot be null or empty")
	}
	if len(ingredients) == 0 {
		return RegisterRecipeCommand{}, fmt.Errorf("recipe must have at least one ingredient")
	}

	return RegisterRecipeCommand{
		Name:        name,
		Ingredients: ingredients,
	}, nil
}