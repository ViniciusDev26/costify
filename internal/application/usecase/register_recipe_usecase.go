package usecase

import (
	"github.com/vini/costify-go/internal/application/contract"
	"github.com/vini/costify-go/internal/application/dto/command"
	"github.com/vini/costify-go/internal/application/dto/entity"
	"github.com/vini/costify-go/internal/application/errors"
	"github.com/vini/costify-go/internal/application/factory"
	"github.com/vini/costify-go/internal/application/service"
	domainservice "github.com/vini/costify-go/internal/domain/service"
	"github.com/vini/costify-go/internal/domain/valueobject"
)

// RegisterRecipeUseCase handles the registration of new recipes
type RegisterRecipeUseCase struct {
	recipeRepository         contract.RecipeRepository
	ingredientLoaderService  *service.IngredientLoaderService
	recipeFactory            *factory.RecipeFactory
	costCalculationService   *domainservice.RecipeCostCalculationService
}

// NewRegisterRecipeUseCase creates a new RegisterRecipeUseCase
func NewRegisterRecipeUseCase(
	recipeRepository contract.RecipeRepository,
	ingredientLoaderService *service.IngredientLoaderService,
	recipeFactory *factory.RecipeFactory,
	costCalculationService *domainservice.RecipeCostCalculationService,
) *RegisterRecipeUseCase {
	return &RegisterRecipeUseCase{
		recipeRepository:        recipeRepository,
		ingredientLoaderService: ingredientLoaderService,
		recipeFactory:           recipeFactory,
		costCalculationService:  costCalculationService,
	}
}

// Execute executes the register recipe use case
func (uc *RegisterRecipeUseCase) Execute(cmd command.RegisterRecipeCommand) (entity.RecipeDto, error) {
	// Validate that recipe doesn't already exist
	exists, err := uc.recipeRepository.ExistsByName(cmd.Name)
	if err != nil {
		return entity.RecipeDto{}, err
	}
	if exists {
		return entity.RecipeDto{}, errors.NewRecipeAlreadyExistsError(
			"Recipe with name '" + cmd.Name + "' already exists")
	}

	// Convert command ingredients to value objects
	var recipeIngredients []valueobject.RecipeIngredient
	for _, cmdIng := range cmd.Ingredients {
		id := valueobject.Id{}.Of(cmdIng.IngredientId)
		unit, exists := valueobject.FromString(cmdIng.Unit)
		if !exists {
			return entity.RecipeDto{}, errors.NewInvalidUnitError("Invalid unit: " + cmdIng.Unit)
		}
		
		recipeIngredient, err := valueobject.NewRecipeIngredient(id, cmdIng.Quantity, unit)
		if err != nil {
			return entity.RecipeDto{}, err
		}
		recipeIngredients = append(recipeIngredients, recipeIngredient)
	}

	// Load ingredients and calculate cost
	ingredientMap, err := uc.ingredientLoaderService.LoadIngredients(recipeIngredients)
	if err != nil {
		return entity.RecipeDto{}, err
	}

	// Create temporary recipe with zero cost for cost calculation
	zeroMoney := valueobject.Money{}.Zero()
	tempRecipe, err := uc.recipeFactory.Create(cmd.Name, recipeIngredients, zeroMoney)
	if err != nil {
		return entity.RecipeDto{}, err
	}

	// Calculate recipe cost
	recipeCost, err := uc.costCalculationService.CalculateCost(tempRecipe, ingredientMap)
	if err != nil {
		return entity.RecipeDto{}, err
	}

	// Create final recipe with calculated total cost
	recipe, err := uc.recipeFactory.Create(cmd.Name, recipeIngredients, recipeCost.TotalCost())
	if err != nil {
		return entity.RecipeDto{}, err
	}

	// Save recipe
	savedRecipe, err := uc.recipeRepository.Save(recipe)
	if err != nil {
		return entity.RecipeDto{}, err
	}

	return entity.FromRecipe(savedRecipe), nil
}