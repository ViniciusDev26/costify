package valueobject

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

var (
	// Volume units (base: ML)
	ML   = Unit{"ML", 1.0, Volume}
	L    = Unit{"L", 1000.0, Volume}    // 1L = 1000ml
	TBSP = Unit{"TBSP", 15.0, Volume}   // 1 tablespoon = 15ml (for liquids)

	// Weight units (base: G)
	G           = Unit{"G", 1.0, Weight}
	KG          = Unit{"KG", 1000.0, Weight}  // 1kg = 1000g
	TBSP_BUTTER = Unit{"TBSP_BUTTER", 14.0, Weight} // 1 tablespoon butter ≈ 14g

	// Count units
	UN = Unit{"UN", 1.0, UnitCount}

	// AllUnits provides a slice of all available units
	AllUnits = []Unit{ML, L, TBSP, G, KG, TBSP_BUTTER, UN}
)

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
	for _, unit := range AllUnits {
		if unit.name == name {
			return unit, true
		}
	}
	return Unit{}, false
}