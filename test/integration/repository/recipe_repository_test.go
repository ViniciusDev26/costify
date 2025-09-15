package repository_test

import (
	"testing"

	"github.com/stretchr/testify/assert"
	"github.com/stretchr/testify/require"
	"github.com/vini/costify-go/internal/domain/entity"
	"github.com/vini/costify-go/internal/domain/valueobject"
	"github.com/vini/costify-go/internal/infrastructure/config"
	"github.com/vini/costify-go/internal/infrastructure/database/repository"
)


func TestRecipeRepository_Save(t *testing.T) {
	// Clean database before test
	require.NoError(t, sharedDB.CleanDatabase())

	recipeRepo := repository.NewRecipeRepository(sharedDB.GetDB())
	ingredientRepo := repository.NewIngredientRepository(sharedDB.GetDB())
	idGenerator := config.NewUuidGenerator()

	// Create test ingredients first
	money1, err := valueobject.NewMoney(10.0)
	require.NoError(t, err)
	ingredient1, err := entity.NewIngredientWithGenerator(
		idGenerator,
		"Flour",
		1.0,
		money1,
		valueobject.KG(),
	)
	require.NoError(t, err)
	savedIngredient1, err := ingredientRepo.Save(ingredient1)
	require.NoError(t, err)

	money2, err := valueobject.NewMoney(5.0)
	require.NoError(t, err)
	ingredient2, err := entity.NewIngredientWithGenerator(
		idGenerator,
		"Sugar",
		1.0,
		money2,
		valueobject.KG(),
	)
	require.NoError(t, err)
	savedIngredient2, err := ingredientRepo.Save(ingredient2)
	require.NoError(t, err)

	// Create recipe ingredients
	recipeIngredient1, err := valueobject.NewRecipeIngredient(
		savedIngredient1.Id(),
		0.5, // 500g
		valueobject.G(),
	)
	require.NoError(t, err)

	recipeIngredient2, err := valueobject.NewRecipeIngredient(
		savedIngredient2.Id(),
		0.2, // 200g
		valueobject.G(),
	)
	require.NoError(t, err)

	ingredients := []valueobject.RecipeIngredient{recipeIngredient1, recipeIngredient2}

	// Create recipe
	totalCost, err := valueobject.NewMoney(7.0)
	require.NoError(t, err)

	recipe, err := entity.NewRecipeWithGenerator(
		idGenerator,
		"Test Recipe",
		ingredients,
		totalCost,
	)
	require.NoError(t, err)

	// Save recipe
	savedRecipe, err := recipeRepo.Save(recipe)
	require.NoError(t, err)

	// Verify saved recipe
	assert.Equal(t, recipe.Id().Value(), savedRecipe.Id().Value())
	assert.Equal(t, "Test Recipe", savedRecipe.Name())
	assert.Len(t, savedRecipe.Ingredients(), 2)
	assert.True(t, savedRecipe.TotalCost().Equals(totalCost))

	// Verify ingredients are loaded
	for _, ingredient := range savedRecipe.Ingredients() {
		assert.NotEmpty(t, ingredient.IngredientId().Value())
		assert.Greater(t, ingredient.Quantity(), 0.0)
	}
}

func TestRecipeRepository_FindById(t *testing.T) {
	// Clean database before test
	require.NoError(t, sharedDB.CleanDatabase())

	recipeRepo := repository.NewRecipeRepository(sharedDB.GetDB())
	ingredientRepo := repository.NewIngredientRepository(sharedDB.GetDB())
	idGenerator := config.NewUuidGenerator()

	// Create test ingredient
	money, err := valueobject.NewMoney(8.0)
	require.NoError(t, err)
	ingredient, err := entity.NewIngredientWithGenerator(
		idGenerator,
		"Milk",
		1.0,
		money,
		valueobject.L(),
	)
	require.NoError(t, err)
	savedIngredient, err := ingredientRepo.Save(ingredient)
	require.NoError(t, err)

	// Create recipe ingredient
	recipeIngredient, err := valueobject.NewRecipeIngredient(
		savedIngredient.Id(),
		0.3, // 300ml
		valueobject.ML(),
	)
	require.NoError(t, err)

	ingredients := []valueobject.RecipeIngredient{recipeIngredient}

	// Create and save recipe
	totalCost, err := valueobject.NewMoney(2.4)
	require.NoError(t, err)

	recipe, err := entity.NewRecipeWithGenerator(
		idGenerator,
		"Find Test Recipe",
		ingredients,
		totalCost,
	)
	require.NoError(t, err)

	savedRecipe, err := recipeRepo.Save(recipe)
	require.NoError(t, err)

	// Find recipe by ID
	foundRecipe, err := recipeRepo.FindById(savedRecipe.Id())
	require.NoError(t, err)

	// Verify found recipe
	assert.Equal(t, savedRecipe.Id().Value(), foundRecipe.Id().Value())
	assert.Equal(t, "Find Test Recipe", foundRecipe.Name())
	assert.Len(t, foundRecipe.Ingredients(), 1)
	assert.True(t, foundRecipe.TotalCost().Equals(totalCost))

	// Verify ingredient details
	foundIngredient := foundRecipe.Ingredients()[0]
	assert.Equal(t, savedIngredient.Id().Value(), foundIngredient.IngredientId().Value())
	assert.Equal(t, 0.3, foundIngredient.Quantity())
	assert.Equal(t, valueobject.ML(), foundIngredient.Unit())
}

func TestRecipeRepository_FindById_NotFound(t *testing.T) {
	// Clean database before test
	require.NoError(t, sharedDB.CleanDatabase())

	recipeRepo := repository.NewRecipeRepository(sharedDB.GetDB())
	nonExistentId := valueobject.Id{}.Of("non-existent-id")

	// Try to find non-existent recipe
	_, err := recipeRepo.FindById(nonExistentId)
	assert.Error(t, err)
	assert.Contains(t, err.Error(), "recipe not found")
}

func TestRecipeRepository_ExistsByName(t *testing.T) {
	// Clean database before test
	require.NoError(t, sharedDB.CleanDatabase())

	recipeRepo := repository.NewRecipeRepository(sharedDB.GetDB())
	ingredientRepo := repository.NewIngredientRepository(sharedDB.GetDB())
	idGenerator := config.NewUuidGenerator()

	// Create test ingredient
	money, err := valueobject.NewMoney(12.0)
	require.NoError(t, err)
	ingredient, err := entity.NewIngredientWithGenerator(
		idGenerator,
		"Eggs",
		12.0,
		money,
		valueobject.UN(),
	)
	require.NoError(t, err)
	savedIngredient, err := ingredientRepo.Save(ingredient)
	require.NoError(t, err)

	// Create recipe ingredient
	recipeIngredient, err := valueobject.NewRecipeIngredient(
		savedIngredient.Id(),
		2.0, // 2 eggs
		valueobject.UN(),
	)
	require.NoError(t, err)

	ingredients := []valueobject.RecipeIngredient{recipeIngredient}

	// Create and save recipe
	totalCost, err := valueobject.NewMoney(2.0)
	require.NoError(t, err)

	recipe, err := entity.NewRecipeWithGenerator(
		idGenerator,
		"Exists Test Recipe",
		ingredients,
		totalCost,
	)
	require.NoError(t, err)

	_, err = recipeRepo.Save(recipe)
	require.NoError(t, err)

	// Check if recipe exists by name
	exists, err := recipeRepo.ExistsByName("Exists Test Recipe")
	require.NoError(t, err)
	assert.True(t, exists)

	// Check non-existent recipe
	exists, err = recipeRepo.ExistsByName("Non Existent Recipe")
	require.NoError(t, err)
	assert.False(t, exists)
}

func TestRecipeRepository_DeleteById(t *testing.T) {
	// Clean database before test
	require.NoError(t, sharedDB.CleanDatabase())

	recipeRepo := repository.NewRecipeRepository(sharedDB.GetDB())
	ingredientRepo := repository.NewIngredientRepository(sharedDB.GetDB())
	idGenerator := config.NewUuidGenerator()

	// Create test ingredient
	money, err := valueobject.NewMoney(6.0)
	require.NoError(t, err)
	ingredient, err := entity.NewIngredientWithGenerator(
		idGenerator,
		"Salt",
		1.0,
		money,
		valueobject.KG(),
	)
	require.NoError(t, err)
	savedIngredient, err := ingredientRepo.Save(ingredient)
	require.NoError(t, err)

	// Create recipe ingredient
	recipeIngredient, err := valueobject.NewRecipeIngredient(
		savedIngredient.Id(),
		0.01, // 10g
		valueobject.G(),
	)
	require.NoError(t, err)

	ingredients := []valueobject.RecipeIngredient{recipeIngredient}

	// Create and save recipe
	totalCost, err := valueobject.NewMoney(0.06)
	require.NoError(t, err)

	recipe, err := entity.NewRecipeWithGenerator(
		idGenerator,
		"Delete Test Recipe",
		ingredients,
		totalCost,
	)
	require.NoError(t, err)

	savedRecipe, err := recipeRepo.Save(recipe)
	require.NoError(t, err)

	// Delete recipe
	err = recipeRepo.DeleteById(savedRecipe.Id())
	require.NoError(t, err)

	// Verify recipe is deleted
	_, err = recipeRepo.FindById(savedRecipe.Id())
	assert.Error(t, err)
	assert.Contains(t, err.Error(), "recipe not found")
}

func TestRecipeRepository_DeleteById_NotFound(t *testing.T) {
	// Clean database before test
	require.NoError(t, sharedDB.CleanDatabase())

	recipeRepo := repository.NewRecipeRepository(sharedDB.GetDB())
	nonExistentId := valueobject.Id{}.Of("non-existent-id")

	// Try to delete non-existent recipe
	err := recipeRepo.DeleteById(nonExistentId)
	assert.Error(t, err)
	assert.Contains(t, err.Error(), "recipe not found")
}
