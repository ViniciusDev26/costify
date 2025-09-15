package entity_test

import (
	"testing"

	"github.com/stretchr/testify/assert"
	"github.com/vini/costify-go/internal/domain/entity"
	"github.com/vini/costify-go/internal/domain/valueobject"
	"github.com/vini/costify-go/internal/infrastructure/config"
)

func TestIngredient_NewIngredient(t *testing.T) {
	id := valueobject.Id{}.Of("test-id")
	money, _ := valueobject.NewMoney(10.50)
	unit := valueobject.KG()

	ingredient, err := entity.NewIngredient(id, "Test Ingredient", 1.0, money, unit)

	assert.NoError(t, err)
	assert.Equal(t, "Test Ingredient", ingredient.Name())
	assert.Equal(t, 1.0, ingredient.PackageQuantity())
	assert.True(t, ingredient.PackagePrice().Equals(money))
	assert.Equal(t, unit, ingredient.PackageUnit())
}

func TestIngredient_NewIngredient_InvalidName(t *testing.T) {
	id := valueobject.Id{}.Of("test-id")
	money, _ := valueobject.NewMoney(10.50)
	unit := valueobject.KG()

	_, err := entity.NewIngredient(id, "", 1.0, money, unit)

	assert.Error(t, err)
}

func TestIngredient_NewIngredient_InvalidQuantity(t *testing.T) {
	id := valueobject.Id{}.Of("test-id")
	money, _ := valueobject.NewMoney(10.50)
	unit := valueobject.KG()

	_, err := entity.NewIngredient(id, "Test Ingredient", 0.0, money, unit)

	assert.Error(t, err)
}

func TestIngredient_NewIngredientWithGenerator(t *testing.T) {
	idGenerator := config.NewUuidGenerator()
	money, _ := valueobject.NewMoney(10.50)
	unit := valueobject.KG()

	ingredient, err := entity.NewIngredientWithGenerator(idGenerator, "Test Ingredient", 1.0, money, unit)

	assert.NoError(t, err)
	assert.Equal(t, "Test Ingredient", ingredient.Name())
	assert.NotEmpty(t, ingredient.Id().Value())
}

func TestIngredient_UnitCost(t *testing.T) {
	id := valueobject.Id{}.Of("test-id")
	money, _ := valueobject.NewMoney(10.0) // $10 for 1000g (1kg)
	unit := valueobject.KG()

	ingredient, _ := entity.NewIngredient(id, "Test Ingredient", 1.0, money, unit)

	// Unit cost should be $10 / 1000g = $0.01 per gram
	expectedUnitCost := 10.0 / 1000.0
	assert.Equal(t, expectedUnitCost, ingredient.UnitCost())
}
