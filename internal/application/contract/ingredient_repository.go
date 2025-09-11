package contract

import (
	"github.com/vini/costify-go/internal/domain/entity"
	"github.com/vini/costify-go/internal/domain/valueobject"
)

// IngredientRepository defines the contract for ingredient persistence operations
type IngredientRepository interface {
	Save(ingredient *entity.Ingredient) (*entity.Ingredient, error)
	FindById(id valueobject.Id) (*entity.Ingredient, error)
	ExistsByName(name string) (bool, error)
	DeleteById(id valueobject.Id) error
}
