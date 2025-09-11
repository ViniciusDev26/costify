package command

// RecipeIngredientCommand represents an ingredient in a recipe command
type RecipeIngredientCommand struct {
	IngredientId string  `json:"ingredientId" validate:"required"`
	Quantity     float64 `json:"quantity" validate:"required,gt=0"`
	Unit         string  `json:"unit" validate:"required"`
}