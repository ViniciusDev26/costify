package models

import (
	"time"

	"github.com/vini/costify-go/internal/domain/entity"
	"github.com/vini/costify-go/internal/domain/valueobject"
)

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
