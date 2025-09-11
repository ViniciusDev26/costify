package errors

// DomainError represents a base domain error
type DomainError struct {
	message string
	code    string
}

func (e DomainError) Error() string {
	return e.message
}

func (e DomainError) Code() string {
	return e.code
}

// Money errors
func NewNegativeMoneyError(message string) error {
	return DomainError{
		message: message,
		code:    "NEGATIVE_MONEY",
	}
}

// Recipe errors
func NewInvalidQuantityError(message string) error {
	return DomainError{
		message: message,
		code:    "INVALID_QUANTITY",
	}
}

func NewEmptyRecipeError(message string) error {
	return DomainError{
		message: message,
		code:    "EMPTY_RECIPE",
	}
}

func NewInvalidTotalCostError(message string) error {
	return DomainError{
		message: message,
		code:    "INVALID_TOTAL_COST",
	}
}

// Ingredient errors
func NewInvalidIngredientNameError(message string) error {
	return DomainError{
		message: message,
		code:    "INVALID_INGREDIENT_NAME",
	}
}
