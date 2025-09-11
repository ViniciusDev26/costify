package database

import (
	"fmt"
	"time"

	"github.com/vini/costify-go/internal/domain/entity"
	"github.com/vini/costify-go/internal/domain/valueobject"
)

// IngredientTable represents the ingredients table model for GORM
type IngredientTable struct {
	ID              string    `gorm:"primaryKey;column:id"`
	Name            string    `gorm:"not null;unique;column:name"`
	PackageQuantity float64   `gorm:"not null;column:package_quantity;check:package_quantity > 0"`
	PackagePrice    float64   `gorm:"not null;column:package_price;check:package_price >= 0"`
	PackageUnit     string    `gorm:"not null;column:package_unit"`
	CreatedAt       time.Time `gorm:"default:CURRENT_TIMESTAMP;column:created_at"`
	UpdatedAt       time.Time `gorm:"default:CURRENT_TIMESTAMP;column:updated_at"`
}

// TableName returns the table name for GORM
func (IngredientTable) TableName() string {
	return "ingredients"
}

// ToEntity converts GORM model to domain entity
func (it *IngredientTable) ToEntity() (*entity.Ingredient, error) {
	id := valueobject.Id{}.Of(it.ID)

	money, err := valueobject.Money{}.Of(it.PackagePrice)
	if err != nil {
		return nil, err
	}

	unit, exists := valueobject.FromString(it.PackageUnit)
	if !exists {
		return nil, fmt.Errorf("invalid unit: %s", it.PackageUnit)
	}

	return entity.NewIngredient(id, it.Name, it.PackageQuantity, money, unit)
}

// FromEntity converts domain entity to GORM model
func FromIngredientEntity(ingredient *entity.Ingredient) *IngredientTable {
	return &IngredientTable{
		ID:              ingredient.Id().Value(),
		Name:            ingredient.Name(),
		PackageQuantity: ingredient.PackageQuantity(),
		PackagePrice:    ingredient.PackagePrice().Amount(),
		PackageUnit:     ingredient.PackageUnit().Name(),
		CreatedAt:       time.Now(),
		UpdatedAt:       time.Now(),
	}
}

// RecipeTable represents the recipes table model for GORM
type RecipeTable struct {
	ID        string    `gorm:"primaryKey;column:id"`
	Name      string    `gorm:"not null;unique;column:name"`
	TotalCost float64   `gorm:"not null;default:0;column:total_cost;check:total_cost >= 0"`
	CreatedAt time.Time `gorm:"default:CURRENT_TIMESTAMP;column:created_at"`
	UpdatedAt time.Time `gorm:"default:CURRENT_TIMESTAMP;column:updated_at"`

	// Relationships
	RecipeIngredients []RecipeIngredientTable `gorm:"foreignKey:RecipeID;constraint:OnDelete:CASCADE"`
}

// TableName returns the table name for GORM
func (RecipeTable) TableName() string {
	return "recipes"
}

// ToEntity converts GORM model to domain entity
func (rt *RecipeTable) ToEntity() (*entity.Recipe, error) {
	id := valueobject.Id{}.Of(rt.ID)

	var ingredients []valueobject.RecipeIngredient
	for _, ri := range rt.RecipeIngredients {
		ingredient, err := ri.ToValueObject()
		if err != nil {
			return nil, err
		}
		ingredients = append(ingredients, ingredient)
	}

	totalCost, err := valueobject.Money{}.Of(rt.TotalCost)
	if err != nil {
		return nil, err
	}

	return entity.NewRecipe(id, rt.Name, ingredients, totalCost)
}

// FromEntity converts domain entity to GORM model
func FromRecipeEntity(recipe *entity.Recipe) *RecipeTable {
	recipeTable := &RecipeTable{
		ID:        recipe.Id().Value(),
		Name:      recipe.Name(),
		TotalCost: recipe.TotalCost().Amount(),
		CreatedAt: time.Now(),
		UpdatedAt: time.Now(),
	}

	for _, ingredient := range recipe.Ingredients() {
		recipeIngredient := FromRecipeIngredientValueObject(recipe.Id().Value(), ingredient)
		recipeTable.RecipeIngredients = append(recipeTable.RecipeIngredients, *recipeIngredient)
	}

	return recipeTable
}

// RecipeIngredientTable represents the recipe_ingredients table model for GORM
type RecipeIngredientTable struct {
	ID           uint      `gorm:"primaryKey;autoIncrement;column:id"`
	RecipeID     string    `gorm:"not null;column:recipe_id"`
	IngredientID string    `gorm:"not null;column:ingredient_id"`
	Quantity     float64   `gorm:"not null;column:quantity;check:quantity > 0"`
	Unit         string    `gorm:"not null;column:unit"`
	CreatedAt    time.Time `gorm:"default:CURRENT_TIMESTAMP;column:created_at"`
}

// TableName returns the table name for GORM
func (RecipeIngredientTable) TableName() string {
	return "recipe_ingredients"
}

// ToValueObject converts GORM model to domain value object
func (rit *RecipeIngredientTable) ToValueObject() (valueobject.RecipeIngredient, error) {
	ingredientId := valueobject.Id{}.Of(rit.IngredientID)

	unit, exists := valueobject.FromString(rit.Unit)
	if !exists {
		return valueobject.RecipeIngredient{}, fmt.Errorf("invalid unit: %s", rit.Unit)
	}

	return valueobject.NewRecipeIngredient(ingredientId, rit.Quantity, unit)
}

// FromRecipeIngredientValueObject converts domain value object to GORM model
func FromRecipeIngredientValueObject(recipeID string, ri valueobject.RecipeIngredient) *RecipeIngredientTable {
	return &RecipeIngredientTable{
		RecipeID:     recipeID,
		IngredientID: ri.IngredientId().Value(),
		Quantity:     ri.Quantity(),
		Unit:         ri.Unit().Name(),
		CreatedAt:    time.Now(),
	}
}
