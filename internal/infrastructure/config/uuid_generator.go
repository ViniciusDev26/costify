package config

import (
	"github.com/google/uuid"
)

// UuidGenerator implements the IdGenerator contract using Google's UUID library
type UuidGenerator struct{}

// NewUuidGenerator creates a new UuidGenerator instance
func NewUuidGenerator() *UuidGenerator {
	return &UuidGenerator{}
}

// Generate creates a new UUID string
func (u *UuidGenerator) Generate() string {
	return uuid.New().String()
}
