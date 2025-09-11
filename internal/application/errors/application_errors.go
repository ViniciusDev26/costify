package errors


// ApplicationError represents a base application error
type ApplicationError struct {
	message string
	code    string
}

func (e ApplicationError) Error() string {
	return e.message
}

func (e ApplicationError) Code() string {
	return e.code
}

// Ingredient errors
func NewIngredientAlreadyExistsError(message string) error {
	return ApplicationError{
		message: message,
		code:    "INGREDIENT_ALREADY_EXISTS",
	}
}

func NewIngredientNotFoundError(message string) error {
	return ApplicationError{
		message: message,
		code:    "INGREDIENT_NOT_FOUND",
	}
}

// Recipe errors
func NewRecipeAlreadyExistsError(message string) error {
	return ApplicationError{
		message: message,
		code:    "RECIPE_ALREADY_EXISTS",
	}
}

func NewRecipeNotFoundError(message string) error {
	return ApplicationError{
		message: message,
		code:    "RECIPE_NOT_FOUND",
	}
}

// Validation errors
func NewInvalidUnitError(message string) error {
	return ApplicationError{
		message: message,
		code:    "INVALID_UNIT",
	}
}