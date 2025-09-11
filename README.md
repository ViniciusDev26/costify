# Costify Go - Recipe Cost Calculator

A Go implementation of the Costify platform for calculating product costs from recipes using Clean Architecture principles. Migrated from Java Spring Boot to Go with Gin framework.

## 🎯 Project Overview

Costify is a platform that calculates the real cost of recipes based on ingredient quantities, units, and prices. Each ingredient has package information (quantity, price, unit) to compute accurate recipe costs.

## 🏗️ Architecture

This project follows **Clean Architecture** (Hexagonal Architecture) with clear separation of concerns:

```
┌─────────────────────────────────────────────────────────────┐
│                    HTTP Layer (Gin)                         │
│                 (Handlers, DTOs, Router)                    │
└─────────────────┬───────────────────────────────────────────┘
                  │
┌─────────────────┴───────────────────────────────────────────┐
│                  Application Layer                          │
│              (Use Cases, Services, DTOs)                   │
└─────────────────┬───────────────────────────────────────────┘
                  │
┌─────────────────┴───────────────────────────────────────────┐
│                    Domain Layer                             │
│        (Entities, Value Objects, Services)                 │
└─────────────────┬───────────────────────────────────────────┘
                  │
┌─────────────────┴───────────────────────────────────────────┐
│                Infrastructure Layer                         │
│       (GORM Repositories, Database, Config)                │
└─────────────────────────────────────────────────────────────┘
```

## 🚀 Tech Stack

- **Go 1.21**
- **Gin** (HTTP framework)
- **GORM** (ORM)
- **PostgreSQL** (database)
- **golang-migrate** (database migrations)
- **Testify** (testing)
- **UUID** (ID generation)
- **Docker & Docker Compose**

## 📁 Project Structure

```
costify-go/
├── cmd/
│   └── server/           # Application entry point
├── internal/
│   ├── domain/           # Domain Layer (core business logic)
│   │   ├── entity/       # Domain entities
│   │   ├── valueobject/  # Value objects
│   │   ├── service/      # Domain services
│   │   ├── contract/     # Domain contracts
│   │   └── errors/       # Domain errors
│   ├── application/      # Application Layer (use cases)
│   │   ├── usecase/      # Application use cases
│   │   ├── dto/          # Data transfer objects
│   │   ├── factory/      # Entity factories
│   │   ├── service/      # Application services
│   │   ├── contract/     # Repository interfaces
│   │   └── errors/       # Application errors
│   └── infrastructure/   # Infrastructure Layer
│       ├── database/     # GORM models & repositories
│       ├── http/         # HTTP handlers & routing
│       └── config/       # Configuration
├── migrations/           # Database migrations
├── test/                 # Tests
├── docker-compose.yml    # Docker setup
├── Dockerfile           # Container definition
└── Makefile            # Build automation
```

## 🛠️ Getting Started

### Prerequisites

- Go 1.21+
- PostgreSQL 15+
- Docker & Docker Compose (optional)
- Make (optional)

### Setup with Docker (Recommended)

1. **Start services with Docker Compose:**
   ```bash
   make docker-up
   # or
   docker-compose up -d
   ```

2. **Build and start with Docker:**
   ```bash
   make docker-build
   # or
   docker-compose up --build -d
   ```

### Manual Setup

1. **Setup PostgreSQL:**
   ```bash
   # Using Docker
   docker run --name costify-postgres \
     -e POSTGRES_DB=costify \
     -e POSTGRES_USER=postgres \
     -e POSTGRES_PASSWORD=postgres \
     -p 5432:5432 -d postgres:15-alpine
   ```

2. **Install dependencies:**
   ```bash
   go mod download
   ```

3. **Run migrations:**
   ```bash
   make migrate
   # or
   migrate -path migrations -database "postgres://postgres:postgres@localhost:5432/costify?sslmode=disable" up
   ```

4. **Run the application:**
   ```bash
   make run
   # or
   go run ./cmd/server
   ```

## 🧪 Testing

```bash
# Run all tests
make test

# Run tests with verbose output
go test -v ./...

# Run specific test package
go test -v ./test/domain/valueobject
```

## 🔧 Development

### Available Make Commands

```bash
make help          # Show available commands
make build         # Build the application
make run           # Run the application
make test          # Run tests
make clean         # Clean build artifacts
make docker-up     # Start with Docker Compose
make docker-down   # Stop Docker Compose
make migrate       # Run database migrations
make fmt           # Format code
make vet           # Vet code
make lint          # Lint code (requires golangci-lint)
make setup         # Setup development environment
make install-hooks # Install lefthook Git hooks
make uninstall-hooks # Remove lefthook Git hooks
```

### Git Hooks (Quality Assurance)

This project uses **Lefthook** for managing Git hooks, equivalent to Husky in the JavaScript ecosystem. Hooks automatically run quality checks before commits and pushes.

#### 🚀 Quick Setup (New Contributors)

```bash
# Complete setup - works on any machine after git clone
make setup
```

This will:
- Download Go dependencies
- Install **lefthook** automatically
- Configure all Git hooks via `.lefthook.yml`
- Install golangci-lint

#### 🎯 Hooks Configuration

**Pre-commit hooks** (run in parallel):
- **format**: Auto-format code with `go fmt`
- **vet**: Static analysis with `go vet`
- **unit-tests**: Fast unit tests (excludes integration)

**Pre-push hooks** (run sequentially):
- **mod-tidy**: Verify `go mod tidy` is clean
- **lint**: Run `golangci-lint` (5min timeout)
- **tests**: Full test suite including integration
- **build**: Verify application builds

**Commit message validation**:
- Enforces conventional commits format (`feat:`, `fix:`, `docs:`, etc.)

#### 🔧 Manual Hook Management

```bash
# Install only Git hooks
make install-hooks

# Remove Git hooks
make uninstall-hooks

# Test hooks manually
lefthook run pre-commit
lefthook run pre-push

# Skip hooks temporarily
LEFTHOOK=0 git commit -m "skip hooks"
```

### Environment Variables

| Variable    | Default     | Description                |
|-------------|-------------|----------------------------|
| DB_HOST     | localhost   | Database host              |
| DB_PORT     | 5432        | Database port              |
| DB_USER     | postgres    | Database user              |
| DB_PASSWORD | postgres    | Database password          |
| DB_NAME     | costify     | Database name              |
| DB_SSLMODE  | disable     | SSL mode for database      |
| SERVER_PORT | 8080        | HTTP server port           |
| GIN_MODE    | debug       | Gin mode (debug/release)   |

## 📚 API Endpoints

### Ingredients

- **POST** `/api/v1/ingredients` - Register new ingredient
  ```json
  {
    "name": "Sugar",
    "packageQuantity": 1000,
    "packagePrice": 5.50,
    "packageUnit": "G"
  }
  ```

### Recipes

- **POST** `/api/v1/recipes` - Register new recipe
  ```json
  {
    "name": "Chocolate Cake",
    "ingredients": [
      {
        "ingredientId": "ingredient-uuid",
        "quantity": 200,
        "unit": "G"
      }
    ]
  }
  ```

- **GET** `/api/v1/recipes/:id/cost` - Calculate recipe cost

### Health Check

- **GET** `/health` - Application health status

## 📋 Supported Units

- **Volume**: ML, L, TBSP
- **Weight**: G, KG, TBSP_BUTTER  
- **Count**: UN (units)

## 🏗️ Core Features

- ✅ **Ingredient Management** - CRUD operations
- ✅ **Recipe Management** - Create recipes with ingredients
- ✅ **Cost Calculation** - Real-time recipe cost calculation
- ✅ **Unit Conversion** - Automatic unit conversions
- ✅ **Clean Architecture** - Maintainable and testable code
- ✅ **Database Migrations** - Version-controlled schema changes
- ✅ **Docker Support** - Containerized deployment
- ✅ **Comprehensive Testing** - Unit and integration tests

## 🔄 Migration from Java

This Go implementation maintains the exact same business logic and Clean Architecture principles as the original Java Spring Boot version, with the following benefits:

- **Better Performance** - Faster startup and execution
- **Smaller Memory Footprint** - More efficient resource usage
- **Single Binary Deployment** - No JVM required
- **Native Concurrency** - Goroutines for better scalability
- **Simpler Dependencies** - Fewer external libraries

## 🤝 Contributing

1. Fork the repository
2. Create a feature branch
3. Make your changes
4. Add tests
5. Run tests and ensure they pass
6. Submit a pull request

## 📝 License

This project is licensed under the MIT License.