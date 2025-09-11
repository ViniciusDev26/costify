package contract

import (
	"github.com/vini/costify-go/internal/domain/entity"
	"github.com/vini/costify-go/internal/domain/valueobject"
)

// RecipeRepository defines the contract for recipe persistence operations
type RecipeRepository interface {
	Save(recipe *entity.Recipe) (*entity.Recipe, error)
	FindById(id valueobject.Id) (*entity.Recipe, error)
	ExistsByName(name string) (bool, error)
	DeleteById(id valueobject.Id) error
}
