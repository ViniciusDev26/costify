package repository

import (
	"fmt"

	"github.com/vini/costify-go/internal/domain/entity"
	"github.com/vini/costify-go/internal/domain/valueobject"
	"github.com/vini/costify-go/internal/infrastructure/database/models"
	"gorm.io/gorm"
)

// IngredientRepository implements the ingredient repository using GORM
type IngredientRepository struct {
	db *gorm.DB
}

// NewIngredientRepository creates a new IngredientRepository
func NewIngredientRepository(db *gorm.DB) *IngredientRepository {
	return &IngredientRepository{db: db}
}

// Save saves an ingredient to the database
func (r *IngredientRepository) Save(ingredient *entity.Ingredient) (*entity.Ingredient, error) {
	ingredientTable := models.FromIngredientEntity(ingredient)

	if err := r.db.Create(ingredientTable).Error; err != nil {
		return nil, fmt.Errorf("failed to save ingredient: %w", err)
	}

	return ingredient, nil
}

// FindById finds an ingredient by ID
func (r *IngredientRepository) FindById(id valueobject.Id) (*entity.Ingredient, error) {
	var ingredientTable models.IngredientTable

	if err := r.db.Where("id = ?", id.Value()).First(&ingredientTable).Error; err != nil {
		if err == gorm.ErrRecordNotFound {
			return nil, fmt.Errorf("ingredient not found with ID: %s", id.Value())
		}
		return nil, fmt.Errorf("failed to find ingredient: %w", err)
	}

	return ingredientTable.ToEntity()
}

// ExistsByName checks if an ingredient exists by name
func (r *IngredientRepository) ExistsByName(name string) (bool, error) {
	var count int64

	if err := r.db.Model(&models.IngredientTable{}).Where("name = ?", name).Count(&count).Error; err != nil {
		return false, fmt.Errorf("failed to check ingredient existence: %w", err)
	}

	return count > 0, nil
}

// DeleteById deletes an ingredient by ID
func (r *IngredientRepository) DeleteById(id valueobject.Id) error {
	result := r.db.Where("id = ?", id.Value()).Delete(&models.IngredientTable{})

	if result.Error != nil {
		return fmt.Errorf("failed to delete ingredient: %w", result.Error)
	}

	if result.RowsAffected == 0 {
		return fmt.Errorf("ingredient not found with ID: %s", id.Value())
	}

	return nil
}
