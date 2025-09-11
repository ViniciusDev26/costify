# Costify Go - Makefile

.PHONY: help build run test clean docker-up docker-down migrate fmt vet lint

# Default target
help:
	@echo "Available commands:"
	@echo "  build      - Build the application"
	@echo "  run        - Run the application"
	@echo "  test       - Run tests"
	@echo "  clean      - Clean build artifacts"
	@echo "  docker-up  - Start with Docker Compose"
	@echo "  docker-down- Stop Docker Compose"
	@echo "  migrate    - Run database migrations"
	@echo "  fmt        - Format code"
	@echo "  vet        - Vet code"
	@echo "  lint       - Lint code (requires golangci-lint)"

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
	@golangci-lint run

# Initialize Go modules
mod-init:
	@go mod tidy

# Download dependencies
deps:
	@echo "Downloading dependencies..."
	@go mod download

# Setup development environment
setup: deps
	@echo "Setting up development environment..."
	@echo "Make sure to have PostgreSQL running on localhost:5432"
	@echo "Database: costify, User: postgres, Password: postgres"