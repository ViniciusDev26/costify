package valueobject

import (
	"github.com/vini/costify-go/internal/domain/contract"
)

// Id represents a domain identifier value object
type Id struct {
	value string
}

// Of creates an Id from a string value
func (Id) Of(value string) Id {
	return Id{value: value}
}

// Generate creates an Id using the provided generator
func (Id) Generate(generator contract.IdGenerator) Id {
	return Id{value: generator.Generate()}
}

// Value returns the underlying string value
func (id Id) Value() string {
	return id.value
}

// String implements the Stringer interface
func (id Id) String() string {
	return id.value
}

// Equals checks if two Ids are equal
func (id Id) Equals(other Id) bool {
	return id.value == other.value
}