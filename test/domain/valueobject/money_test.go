package valueobject_test

import (
	"testing"

	"github.com/stretchr/testify/assert"
	"github.com/vini/costify-go/internal/domain/valueobject"
)

func TestMoney_Of(t *testing.T) {
	tests := []struct {
		name      string
		amount    float64
		wantError bool
	}{
		{"positive amount", 10.50, false},
		{"zero amount", 0.0, false},
		{"negative amount", -5.0, true},
	}

	for _, tt := range tests {
		t.Run(tt.name, func(t *testing.T) {
			money, err := valueobject.Money{}.Of(tt.amount)

			if tt.wantError {
				assert.Error(t, err)
			} else {
				assert.NoError(t, err)
				assert.Equal(t, tt.amount, money.Amount())
			}
		})
	}
}

func TestMoney_Add(t *testing.T) {
	money1, _ := valueobject.Money{}.Of(10.50)
	money2, _ := valueobject.Money{}.Of(5.25)

	result := money1.Add(money2)

	assert.Equal(t, 15.75, result.Amount())
}

func TestMoney_Subtract(t *testing.T) {
	money1, _ := valueobject.Money{}.Of(10.50)
	money2, _ := valueobject.Money{}.Of(5.25)

	result, err := money1.Subtract(money2)

	assert.NoError(t, err)
	assert.Equal(t, 5.25, result.Amount())
}

func TestMoney_Subtract_NegativeResult(t *testing.T) {
	money1, _ := valueobject.Money{}.Of(5.25)
	money2, _ := valueobject.Money{}.Of(10.50)

	_, err := money1.Subtract(money2)

	assert.Error(t, err)
}

func TestMoney_Multiply(t *testing.T) {
	money, _ := valueobject.Money{}.Of(10.50)

	result, err := money.Multiply(2.0)

	assert.NoError(t, err)
	assert.Equal(t, 21.0, result.Amount())
}

func TestMoney_Divide(t *testing.T) {
	money, _ := valueobject.Money{}.Of(10.50)

	result, err := money.Divide(2.0)

	assert.NoError(t, err)
	assert.Equal(t, 5.25, result.Amount())
}

func TestMoney_Equals(t *testing.T) {
	money1, _ := valueobject.Money{}.Of(10.50)
	money2, _ := valueobject.Money{}.Of(10.50)
	money3, _ := valueobject.Money{}.Of(5.25)

	assert.True(t, money1.Equals(money2))
	assert.False(t, money1.Equals(money3))
}

func TestMoney_Comparisons(t *testing.T) {
	money1, _ := valueobject.Money{}.Of(10.50)
	money2, _ := valueobject.Money{}.Of(5.25)

	assert.True(t, money1.IsGreaterThan(money2))
	assert.False(t, money1.IsLessThan(money2))
	assert.True(t, money2.IsLessThan(money1))
	assert.False(t, money2.IsGreaterThan(money1))
}
