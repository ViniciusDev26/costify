package repository

import (
	"fmt"

	"github.com/vini/costify-go/internal/domain/entity"
	"github.com/vini/costify-go/internal/domain/valueobject"
	"github.com/vini/costify-go/internal/infrastructure/database/models"
	"gorm.io/gorm"
)

// RecipeRepository implements the recipe repository using GORM
type RecipeRepository struct {
	db *gorm.DB
}

// NewRecipeRepository creates a new RecipeRepository
func NewRecipeRepository(db *gorm.DB) *RecipeRepository {
	return &RecipeRepository{db: db}
}

// Save saves a recipe to the database
func (r *RecipeRepository) Save(recipe *entity.Recipe) (*entity.Recipe, error) {
	recipeTable := models.FromRecipeEntity(recipe)

	// Start transaction
	tx := r.db.Begin()
	if tx.Error != nil {
		return nil, fmt.Errorf("failed to start transaction: %w", tx.Error)
	}

	// Save recipe
	if err := tx.Create(recipeTable).Error; err != nil {
		tx.Rollback()
		return nil, fmt.Errorf("failed to save recipe: %w", err)
	}

	// Commit transaction
	if err := tx.Commit().Error; err != nil {
		return nil, fmt.Errorf("failed to commit transaction: %w", err)
	}

	return recipe, nil
}

// FindById finds a recipe by ID
func (r *RecipeRepository) FindById(id valueobject.Id) (*entity.Recipe, error) {
	var recipeTable models.RecipeTable

	if err := r.db.Preload("RecipeIngredients").Where("id = ?", id.Value()).First(&recipeTable).Error; err != nil {
		if err == gorm.ErrRecordNotFound {
			return nil, fmt.Errorf("recipe not found with ID: %s", id.Value())
		}
		return nil, fmt.Errorf("failed to find recipe: %w", err)
	}

	return recipeTable.ToEntity()
}

// ExistsByName checks if a recipe exists by name
func (r *RecipeRepository) ExistsByName(name string) (bool, error) {
	var count int64

	if err := r.db.Model(&models.RecipeTable{}).Where("name = ?", name).Count(&count).Error; err != nil {
		return false, fmt.Errorf("failed to check recipe existence: %w", err)
	}

	return count > 0, nil
}

// DeleteById deletes a recipe by ID
func (r *RecipeRepository) DeleteById(id valueobject.Id) error {
	// Start transaction
	tx := r.db.Begin()
	if tx.Error != nil {
		return fmt.Errorf("failed to start transaction: %w", tx.Error)
	}

	// Delete recipe ingredients first (foreign key constraint)
	if err := tx.Where("recipe_id = ?", id.Value()).Delete(&models.RecipeIngredientTable{}).Error; err != nil {
		tx.Rollback()
		return fmt.Errorf("failed to delete recipe ingredients: %w", err)
	}

	// Delete recipe
	result := tx.Where("id = ?", id.Value()).Delete(&models.RecipeTable{})
	if result.Error != nil {
		tx.Rollback()
		return fmt.Errorf("failed to delete recipe: %w", result.Error)
	}

	if result.RowsAffected == 0 {
		tx.Rollback()
		return fmt.Errorf("recipe not found with ID: %s", id.Value())
	}

	// Commit transaction
	if err := tx.Commit().Error; err != nil {
		return fmt.Errorf("failed to commit transaction: %w", err)
	}

	return nil
}
