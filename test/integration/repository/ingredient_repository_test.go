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


func TestIngredientRepository_Save(t *testing.T) {
	// Clean database before test
	require.NoError(t, sharedDB.CleanDatabase())

	repo := repository.NewIngredientRepository(sharedDB.GetDB())
	idGenerator := config.NewUuidGenerator()

	// Create test ingredient
	money, err := valueobject.NewMoney(10.50)
	require.NoError(t, err)

	ingredient, err := entity.NewIngredientWithGenerator(
		idGenerator,
		"Test Ingredient",
		1.0,
		money,
		valueobject.KG(),
	)
	require.NoError(t, err)

	// Save ingredient
	savedIngredient, err := repo.Save(ingredient)
	require.NoError(t, err)

	// Verify saved ingredient
	assert.Equal(t, ingredient.Id().Value(), savedIngredient.Id().Value())
	assert.Equal(t, "Test Ingredient", savedIngredient.Name())
	assert.Equal(t, 1.0, savedIngredient.PackageQuantity())
	assert.True(t, savedIngredient.PackagePrice().Equals(money))
	assert.Equal(t, valueobject.KG(), savedIngredient.PackageUnit())
}

func TestIngredientRepository_FindById(t *testing.T) {
	// Clean database before test
	require.NoError(t, sharedDB.CleanDatabase())

	repo := repository.NewIngredientRepository(sharedDB.GetDB())
	idGenerator := config.NewUuidGenerator()

	// Create and save test ingredient
	money, err := valueobject.NewMoney(15.75)
	require.NoError(t, err)

	ingredient, err := entity.NewIngredientWithGenerator(
		idGenerator,
		"Find Test Ingredient",
		2.5,
		money,
		valueobject.G(),
	)
	require.NoError(t, err)

	savedIngredient, err := repo.Save(ingredient)
	require.NoError(t, err)

	// Find ingredient by ID
	foundIngredient, err := repo.FindById(savedIngredient.Id())
	require.NoError(t, err)

	// Verify found ingredient
	assert.Equal(t, savedIngredient.Id().Value(), foundIngredient.Id().Value())
	assert.Equal(t, "Find Test Ingredient", foundIngredient.Name())
	assert.Equal(t, 2.5, foundIngredient.PackageQuantity())
	assert.True(t, foundIngredient.PackagePrice().Equals(money))
	assert.Equal(t, valueobject.G(), foundIngredient.PackageUnit())
}

func TestIngredientRepository_FindById_NotFound(t *testing.T) {
	// Clean database before test
	require.NoError(t, sharedDB.CleanDatabase())

	repo := repository.NewIngredientRepository(sharedDB.GetDB())
	nonExistentId := valueobject.Id{}.Of("non-existent-id")

	// Try to find non-existent ingredient
	_, err := repo.FindById(nonExistentId)
	assert.Error(t, err)
	assert.Contains(t, err.Error(), "ingredient not found")
}

func TestIngredientRepository_ExistsByName(t *testing.T) {
	// Clean database before test
	require.NoError(t, sharedDB.CleanDatabase())

	repo := repository.NewIngredientRepository(sharedDB.GetDB())
	idGenerator := config.NewUuidGenerator()

	// Create and save test ingredient
	money, err := valueobject.NewMoney(5.25)
	require.NoError(t, err)

	ingredient, err := entity.NewIngredientWithGenerator(
		idGenerator,
		"Exists Test Ingredient",
		1.0,
		money,
		valueobject.KG(),
	)
	require.NoError(t, err)

	_, err = repo.Save(ingredient)
	require.NoError(t, err)

	// Check if ingredient exists by name
	exists, err := repo.ExistsByName("Exists Test Ingredient")
	require.NoError(t, err)
	assert.True(t, exists)

	// Check non-existent ingredient
	exists, err = repo.ExistsByName("Non Existent Ingredient")
	require.NoError(t, err)
	assert.False(t, exists)
}

func TestIngredientRepository_DeleteById(t *testing.T) {
	// Clean database before test
	require.NoError(t, sharedDB.CleanDatabase())

	repo := repository.NewIngredientRepository(sharedDB.GetDB())
	idGenerator := config.NewUuidGenerator()

	// Create and save test ingredient
	money, err := valueobject.NewMoney(8.90)
	require.NoError(t, err)

	ingredient, err := entity.NewIngredientWithGenerator(
		idGenerator,
		"Delete Test Ingredient",
		1.5,
		money,
		valueobject.G(),
	)
	require.NoError(t, err)

	savedIngredient, err := repo.Save(ingredient)
	require.NoError(t, err)

	// Delete ingredient
	err = repo.DeleteById(savedIngredient.Id())
	require.NoError(t, err)

	// Verify ingredient is deleted
	_, err = repo.FindById(savedIngredient.Id())
	assert.Error(t, err)
	assert.Contains(t, err.Error(), "ingredient not found")
}

func TestIngredientRepository_DeleteById_NotFound(t *testing.T) {
	// Clean database before test
	require.NoError(t, sharedDB.CleanDatabase())

	repo := repository.NewIngredientRepository(sharedDB.GetDB())
	nonExistentId := valueobject.Id{}.Of("non-existent-id")

	// Try to delete non-existent ingredient
	err := repo.DeleteById(nonExistentId)
	assert.Error(t, err)
	assert.Contains(t, err.Error(), "ingredient not found")
}
