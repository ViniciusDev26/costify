package service

import (
	"github.com/vini/costify-go/internal/application/contract"
	"github.com/vini/costify-go/internal/application/errors"
	"github.com/vini/costify-go/internal/domain/entity"
	"github.com/vini/costify-go/internal/domain/valueobject"
)

// IngredientLoaderService loads ingredients for recipe processing
type IngredientLoaderService struct {
	ingredientRepository contract.IngredientRepository
}

// NewIngredientLoaderService creates a new IngredientLoaderService
func NewIngredientLoaderService(ingredientRepository contract.IngredientRepository) *IngredientLoaderService {
	return &IngredientLoaderService{
		ingredientRepository: ingredientRepository,
	}
}

// LoadIngredients loads ingredients from recipe ingredients list
func (s *IngredientLoaderService) LoadIngredients(
	recipeIngredients []valueobject.RecipeIngredient,
) (map[valueobject.Id]*entity.Ingredient, error) {
	ingredientMap := make(map[valueobject.Id]*entity.Ingredient)

	for _, recipeIngredient := range recipeIngredients {
		ingredientId := recipeIngredient.IngredientId()

		ingredient, err := s.ingredientRepository.FindById(ingredientId)
		if err != nil {
			return nil, errors.NewIngredientNotFoundError("Ingredient not found with ID: " + ingredientId.Value())
		}

		ingredientMap[ingredientId] = ingredient
	}

	return ingredientMap, nil
}
