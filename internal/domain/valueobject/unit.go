package valueobject

import (
	"strings"

	"github.com/vini/costify-go/internal/domain/errors"
)

// UnitType represents the type of measurement unit
type UnitType int

const (
	Volume UnitType = iota
	Weight
	UnitCount
)

// Unit represents a measurement unit with conversion to base units
type Unit struct {
	name         string
	factorToBase float64
	unitType     UnitType
}

// NewUnit creates a validated Unit instance
func NewUnit(name string, factorToBase float64, unitType UnitType) (Unit, error) {
	if strings.TrimSpace(name) == "" {
		return Unit{}, errors.NewInvalidUnitNameError("Unit name cannot be empty")
	}
	if factorToBase <= 0 {
		return Unit{}, errors.NewInvalidConversionFactorError("Conversion factor must be positive")
	}
	return Unit{
		name:         strings.TrimSpace(name),
		factorToBase: factorToBase,
		unitType:     unitType,
	}, nil
}

// Predefined units as functions to ensure immutability
func ML() Unit {
	unit, _ := NewUnit("ML", 1.0, Volume)
	return unit
}

func L() Unit {
	unit, _ := NewUnit("L", 1000.0, Volume)
	return unit
}

func TBSP() Unit {
	unit, _ := NewUnit("TBSP", 15.0, Volume)
	return unit
}

func G() Unit {
	unit, _ := NewUnit("G", 1.0, Weight)
	return unit
}

func KG() Unit {
	unit, _ := NewUnit("KG", 1000.0, Weight)
	return unit
}

func TBSP_BUTTER() Unit {
	unit, _ := NewUnit("TBSP_BUTTER", 14.0, Weight)
	return unit
}

func UN() Unit {
	unit, _ := NewUnit("UN", 1.0, UnitCount)
	return unit
}

// AllUnits returns all available units
func AllUnits() []Unit {
	return []Unit{ML(), L(), TBSP(), G(), KG(), TBSP_BUTTER(), UN()}
}

// ToBase converts the given quantity to the base unit for this unit type
func (u Unit) ToBase(quantity float64) float64 {
	return quantity * u.factorToBase
}

// Type returns the unit type
func (u Unit) Type() UnitType {
	return u.unitType
}

// Name returns the unit name
func (u Unit) Name() string {
	return u.name
}

// String implements the Stringer interface
func (u Unit) String() string {
	return u.name
}

// FromString returns a Unit from its string representation
func FromString(name string) (Unit, bool) {
	for _, unit := range AllUnits() {
		if unit.name == name {
			return unit, true
		}
	}
	return Unit{}, false
}
