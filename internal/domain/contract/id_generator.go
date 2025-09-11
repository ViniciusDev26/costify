package contract

// IdGenerator defines the contract for generating unique identifiers
type IdGenerator interface {
	Generate() string
}
