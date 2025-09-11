# Costify Go - Makefile

.PHONY: help build run test clean docker-up docker-down migrate fmt vet lint setup install-hooks uninstall-hooks

# Default target
help:
	@echo "Available commands:"
	@echo "  build         - Build the application"
	@echo "  run           - Run the application"
	@echo "  test          - Run tests"
	@echo "  clean         - Clean build artifacts"
	@echo "  docker-up     - Start with Docker Compose"
	@echo "  docker-down   - Stop Docker Compose"
	@echo "  migrate       - Run database migrations"
	@echo "  fmt           - Format code"
	@echo "  vet           - Vet code"
	@echo "  lint          - Lint code (requires golangci-lint)"
	@echo "  setup         - Setup development environment"
	@echo "  install-hooks - Install lefthook Git hooks"
	@echo "  uninstall-hooks - Remove lefthook Git hooks"

# Build the application
build:
	@echo "Building Costify Go..."
	@go build -o bin/costify ./cmd/server

# Run the application
run:
	@echo "Running Costify Go..."
	@go run ./cmd/server

# Run tests
test:
	@echo "Running tests..."
	@go test -v ./...

# Clean build artifacts
clean:
	@echo "Cleaning up..."
	@rm -rf bin/
	@go clean

# Start with Docker Compose
docker-up:
	@echo "Starting with Docker Compose..."
	@docker-compose up -d

# Stop Docker Compose
docker-down:
	@echo "Stopping Docker Compose..."
	@docker-compose down

# Build and start with Docker Compose
docker-build:
	@echo "Building and starting with Docker Compose..."
	@docker-compose up --build -d

# Run database migrations
migrate:
	@echo "Running database migrations..."
	@migrate -path migrations -database "postgres://postgres:postgres@localhost:5432/costify?sslmode=disable" up

# Format code
fmt:
	@echo "Formatting code..."
	@go fmt ./...

# Vet code
vet:
	@echo "Vetting code..."
	@go vet ./...

# Lint code (requires golangci-lint)
lint:
	@echo "Linting code..."
	@if command -v golangci-lint >/dev/null 2>&1; then \
		golangci-lint run; \
	else \
		echo "golangci-lint not found. Install it with:"; \
		echo "go install github.com/golangci/golangci-lint/cmd/golangci-lint@latest"; \
		exit 1; \
	fi

# Download dependencies
deps:
	@echo "Downloading dependencies..."
	@go mod download

# Install lefthook hooks
install-hooks:
	@echo "Installing lefthook Git hooks..."
	if ! command -v lefthook >/dev/null 2>&1; then \
		echo "Installing lefthook..."; \
		go install github.com/evilmartians/lefthook@latest; \
	fi
	lefthook install
	@echo "✅ Git hooks installed with lefthook!"

# Setup development environment
setup: deps install-hooks
	@echo "Setting up development environment..."
	@if ! command -v golangci-lint >/dev/null 2>&1; then \
		echo "Installing golangci-lint..."; \
		go install github.com/golangci/golangci-lint/cmd/golangci-lint@latest; \
	fi
	@echo "✅ Development environment ready!"

# Remove lefthook hooks
uninstall-hooks:
	@echo "Uninstalling lefthook Git hooks..."
	lefthook uninstall
	@echo "✅ Git hooks removed!"
