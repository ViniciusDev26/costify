package models

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
