package usecase

import (
	"github.com/vini/costify-go/internal/application/contract"
	"github.com/vini/costify-go/internal/application/dto/command"
	"github.com/vini/costify-go/internal/application/dto/entity"
	"github.com/vini/costify-go/internal/application/errors"
	"github.com/vini/costify-go/internal/application/factory"
)

// RegisterIngredientUseCase handles the registration of new ingredients
type RegisterIngredientUseCase struct {
	ingredientRepository contract.IngredientRepository
	ingredientFactory    *factory.IngredientFactory
}

// NewRegisterIngredientUseCase creates a new RegisterIngredientUseCase
func NewRegisterIngredientUseCase(
	ingredientRepository contract.IngredientRepository,
	ingredientFactory *factory.IngredientFactory,
) *RegisterIngredientUseCase {
	return &RegisterIngredientUseCase{
		ingredientRepository: ingredientRepository,
		ingredientFactory:    ingredientFactory,
	}
}

// Execute executes the register ingredient use case
func (uc *RegisterIngredientUseCase) Execute(cmd command.RegisterIngredientCommand) (entity.IngredientDto, error) {
	// Validate that ingredient doesn't already exist
	exists, err := uc.ingredientRepository.ExistsByName(cmd.Name)
	if err != nil {
		return entity.IngredientDto{}, err
	}
	if exists {
		return entity.IngredientDto{}, errors.NewIngredientAlreadyExistsError(
			"Ingredient with name '" + cmd.Name + "' already exists")
	}

	// Create ingredient using factory
	ingredient, err := uc.ingredientFactory.Create(
		cmd.Name,
		cmd.PackageQuantity,
		cmd.PackagePrice,
		cmd.PackageUnit,
	)
	if err != nil {
		return entity.IngredientDto{}, err
	}

	// Save ingredient
	savedIngredient, err := uc.ingredientRepository.Save(ingredient)
	if err != nil {
		return entity.IngredientDto{}, err
	}

	return entity.FromIngredient(savedIngredient), nil
}
