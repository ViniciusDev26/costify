package usecase

import (
	"github.com/vini/costify-go/internal/application/contract"
	"github.com/vini/costify-go/internal/application/dto/response"
	"github.com/vini/costify-go/internal/application/errors"
	"github.com/vini/costify-go/internal/application/service"
	domainservice "github.com/vini/costify-go/internal/domain/service"
	"github.com/vini/costify-go/internal/domain/valueobject"
)

// CalculateRecipeCostUseCase handles recipe cost calculation
type CalculateRecipeCostUseCase struct {
	recipeRepository        contract.RecipeRepository
	ingredientLoaderService *service.IngredientLoaderService
	costCalculationService  *domainservice.RecipeCostCalculationService
}

// NewCalculateRecipeCostUseCase creates a new CalculateRecipeCostUseCase
func NewCalculateRecipeCostUseCase(
	recipeRepository contract.RecipeRepository,
	ingredientLoaderService *service.IngredientLoaderService,
	costCalculationService *domainservice.RecipeCostCalculationService,
) *CalculateRecipeCostUseCase {
	return &CalculateRecipeCostUseCase{
		recipeRepository:        recipeRepository,
		ingredientLoaderService: ingredientLoaderService,
		costCalculationService:  costCalculationService,
	}
}

// Execute executes the calculate recipe cost use case
func (uc *CalculateRecipeCostUseCase) Execute(recipeId string) (response.RecipeCostDto, error) {
	id := valueobject.Id{}.Of(recipeId)

	recipe, err := uc.recipeRepository.FindById(id)
	if err != nil {
		return response.RecipeCostDto{}, errors.NewRecipeNotFoundError("Recipe not found with ID: " + recipeId)
	}

	ingredientMap, err := uc.ingredientLoaderService.LoadIngredients(recipe.Ingredients())
	if err != nil {
		return response.RecipeCostDto{}, err
	}

	recipeCost, err := uc.costCalculationService.CalculateCost(recipe, ingredientMap)
	if err != nil {
		return response.RecipeCostDto{}, err
	}

	return response.FromRecipeCost(recipeCost), nil
}
