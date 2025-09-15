package valueobject

import (
	"fmt"
	"math"

	"github.com/vini/costify-go/internal/domain/errors"
)

// Money represents a monetary value with business rules
type Money struct {
	amount float64
}

const (
	// Scale for monetary calculations (2 decimal places)
	MoneyScale = 100.0
)

// NewMoney creates a Money instance from a float64 amount
func NewMoney(amount float64) (Money, error) {
	if amount < 0 {
		return Money{}, errors.NewNegativeMoneyError("Money cannot be negative in business context")
	}
	// Round to 2 decimal places
	rounded := math.Round(amount*MoneyScale) / MoneyScale
	return Money{amount: rounded}, nil
}

// ZeroMoney creates a Money instance with zero value
func ZeroMoney() Money {
	return Money{amount: 0.0}
}

// Add adds two Money values
func (m Money) Add(other Money) Money {
	result := m.amount + other.amount
	rounded := math.Round(result*MoneyScale) / MoneyScale
	return Money{amount: rounded}
}

// Subtract subtracts another Money value from this one
func (m Money) Subtract(other Money) (Money, error) {
	result := m.amount - other.amount
	if result < 0 {
		return Money{}, errors.NewNegativeMoneyError("Subtraction would result in negative money")
	}
	rounded := math.Round(result*MoneyScale) / MoneyScale
	return Money{amount: rounded}, nil
}

// Multiply multiplies Money by a multiplier
func (m Money) Multiply(multiplier float64) (Money, error) {
	result := m.amount * multiplier
	if result < 0 {
		return Money{}, errors.NewNegativeMoneyError("Multiplication would result in negative money")
	}
	rounded := math.Round(result*MoneyScale) / MoneyScale
	return Money{amount: rounded}, nil
}

// Divide divides Money by a divisor
func (m Money) Divide(divisor float64) (Money, error) {
	if divisor == 0 {
		return Money{}, errors.NewDivisionByZeroError("Cannot divide money by zero")
	}
	result := m.amount / divisor
	if result < 0 {
		return Money{}, errors.NewNegativeMoneyError("Division would result in negative money")
	}
	rounded := math.Round(result*MoneyScale) / MoneyScale
	return Money{amount: rounded}, nil
}

// IsGreaterThan checks if this Money is greater than another
func (m Money) IsGreaterThan(other Money) bool {
	return m.amount > other.amount
}

// IsLessThan checks if this Money is less than another
func (m Money) IsLessThan(other Money) bool {
	return m.amount < other.amount
}

// Equals checks if two Money values are equal
func (m Money) Equals(other Money) bool {
	return math.Abs(m.amount-other.amount) < 0.001 // Handle floating point precision
}

// Amount returns the monetary amount as float64
func (m Money) Amount() float64 {
	return m.amount
}

// String implements the Stringer interface
func (m Money) String() string {
	return fmt.Sprintf("$%.2f", m.amount)
}
