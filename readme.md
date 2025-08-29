# Costify

Costify is a Java platform for calculating product costs from recipes. Each ingredient has quantity, unit, and price data to compute real recipe costs. Built with Clean Architecture principles and Spring Boot.

---

## Features

### 🚀 Core Functionality
* **Recipe Management** - Complete CRUD operations for recipes with multiple ingredients
* **Ingredient Management** - Full ingredient registration and management system  
* **Cost Calculation Engine** - Real-time recipe cost calculation with ingredient breakdown
* **REST API** - HTTP endpoints for all operations with JSON request/response

### 🏗️ Architecture
* **Clean Architecture** with Domain, Application, and Infrastructure layers
* **Domain-Driven Design** with proper aggregates, entities, and value objects
* **Repository Pattern** with dependency inversion and PostgreSQL persistence
* **Use Case Pattern** for business workflows and validation
* **Factory Pattern** for proper domain object creation

### 🧪 Testing & Quality
* **Comprehensive Test Suite** with 20+ test classes covering all layers
* **Integration Testing** with real PostgreSQL database via Testcontainers
* **Unit Testing** for domain logic, use cases, and DTOs
* **Database Constraint Testing** with repository integration tests
* **Migration Testing** with Flyway schema evolution verification

---

## Technology Stack

* **Java 21** - Programming language
* **Spring Boot 3.5.5** - Application framework with embedded Tomcat
* **Maven** - Build and dependency management  
* **PostgreSQL** - Primary database with ACID transactions
* **Flyway** - Database schema migration management
* **JUnit 5** - Unit and integration testing framework
* **Mockito** - Mocking framework for unit tests
* **Testcontainers** - Integration testing with containerized PostgreSQL
* **Spring Security** - Authentication and authorization
* **Lombok** - Boilerplate code reduction

---

## Getting Started

### Prerequisites

* **Java 21** or higher
* **Maven 3.6+** or use the included Maven wrapper
* **Docker** (for running PostgreSQL database)
* **Git** for version control

### Running the Project

Clone the repository:

```bash
git clone https://github.com/your-username/costify.git
cd costify
```

Start the PostgreSQL database:

```bash
docker-compose up -d postgres
```

Build and run the application:

```bash
./mvnw clean install
./mvnw spring-boot:run
```

### Running Tests

```bash
# Run all tests with Java assertions enabled  
./mvnw test -DargLine="-ea"

# Run only unit tests (exclude integration tests)
./mvnw test -DargLine="-ea" -Dtest="!**/*IntegrationTest"

# Run only integration tests
./mvnw test -DargLine="-ea" -Dtest="**/*IntegrationTest"

# Run specific test class
./mvnw test -DargLine="-ea" -Dtest=IngredientTest

# Clean build with all tests
./mvnw clean install
```

Java assertions are required for domain validation tests. All tests should pass consistently.

---

## API Endpoints

### 🎯 Available REST API

Once the application is running (default: http://localhost:8080), the following endpoints are available:

#### **Ingredients API**

**Register New Ingredient**
```http
POST /ingredients
Content-Type: application/json

{
  "name": "Flour",
  "packageQuantity": 1.0,
  "packagePrice": 2.50,
  "packageUnit": "KG"
}
```

**Response:**
```json
{
  "id": "550e8400-e29b-41d4-a716-446655440000",
  "name": "Flour",
  "packageQuantity": 1.0,
  "packagePrice": 2.50,
  "packageUnit": "KG",
  "unitCost": 2.50
}
```

#### **Recipes API**

**Register New Recipe**
```http
POST /recipes
Content-Type: application/json

{
  "name": "Bread",
  "ingredients": [
    {
      "ingredientId": "550e8400-e29b-41d4-a716-446655440000",
      "quantity": 0.5,
      "unit": "KG"
    },
    {
      "ingredientId": "660e8400-e29b-41d4-a716-446655440001",
      "quantity": 200.0,
      "unit": "ML"
    }
  ]
}
```

**Response:**
```json
{
  "id": "770e8400-e29b-41d4-a716-446655440002",
  "name": "Bread",
  "ingredients": [
    {
      "ingredientId": "550e8400-e29b-41d4-a716-446655440000",
      "quantity": 0.5,
      "unit": "KG"
    },
    {
      "ingredientId": "660e8400-e29b-41d4-a716-446655440001", 
      "quantity": 200.0,
      "unit": "ML"
    }
  ]
}
```

### 📋 Supported Units

The API supports the following measurement units:

- **Volume**: `ML` (milliliters), `L` (liters), `TBSP` (tablespoons = 15ml for liquids)
- **Weight**: `G` (grams), `KG` (kilograms), `TBSP_BUTTER` (tablespoons ≈ 14g for butter/margarine)
- **Unit**: `UN` (units/pieces)

> **Note**: Use `TBSP` for liquid ingredients (vanilla extract, oil, milk) and `TBSP_BUTTER` for solid fats (butter, margarine) since they have different densities.

### ⚠️ Validation Rules

**Ingredient Registration:**
- `name`: Required, cannot be blank
- `packageQuantity`: Must be ≥ 1.0
- `packagePrice`: Must be ≥ 1.0  
- `packageUnit`: Required, must be valid Unit enum

**Recipe Registration:**
- `name`: Required, cannot be blank
- `ingredients`: Required, cannot be empty
- `ingredientId`: Required, must be valid UUID
- `quantity`: Must be ≥ 0.01
- `unit`: Required, must be valid Unit enum

### 🔍 HTTP Client Examples

See the `api-client/` folder for ready-to-use HTTP request files compatible with popular IDEs and tools.

## Project Structure

```
src/
├── main/
│   ├── java/br/unifor/costify/
│   │   ├── CostifyApplication.java          # Spring Boot main class
│   │   ├── application/                     # ✅ Application Layer (Complete)
│   │   │   ├── contracts/                   # Repository interfaces
│   │   │   ├── dto/command/                 # Input DTOs (RegisterIngredient/RecipeCommand)
│   │   │   ├── dto/entity/                  # Output DTOs (Ingredient/RecipeDto)  
│   │   │   ├── dto/response/                # Response DTOs (Cost calculation)
│   │   │   ├── errors/                      # Application exceptions
│   │   │   ├── factory/                     # Domain object factories
│   │   │   ├── service/                     # Application services
│   │   │   ├── usecase/                     # Business workflows (4 use cases)
│   │   │   └── validation/                  # Input validation service
│   │   ├── domain/                          # ✅ Domain Layer (Complete) 
│   │   │   ├── contracts/                   # Domain contracts (IdGenerator)
│   │   │   ├── entity/                      # Aggregates (Ingredient, Recipe)
│   │   │   ├── errors/                      # Domain exceptions hierarchy
│   │   │   ├── events/                      # Domain events (ready for expansion)
│   │   │   ├── service/                     # Domain services (Cost calculation)
│   │   │   └── valueobject/                 # Value objects (Id, Money, Unit, etc.)
│   │   ├── infra/                           # ✅ Infrastructure Layer (Complete)
│   │   │   ├── config/                      # Security & UUID generation config
│   │   │   ├── controllers/                 # REST endpoints (Ingredient/Recipe) 
│   │   │   └── data/                        # JPA entities & PostgreSQL repositories
│   │   └── infrastructure/                  # Additional infrastructure (expandable)
│   └── resources/
│       ├── application.properties           # Spring Boot configuration
│       └── db/migration/                    # ✅ 4 Flyway migration files
└── test/                                    # ✅ 20+ comprehensive test classes
    ├── integration/                         # Database & repository integration tests
    ├── application/                         # Use case & DTO tests  
    └── domain/                              # Domain logic unit tests
```

## Architecture Overview

This project follows **Clean Architecture** principles with clear separation of concerns:

### 🏗️ Implemented Layers

1. **Domain Layer** - Core business logic (✅ Complete)
   - Entities: `Ingredient`, `Recipe`
   - Value Objects: `Id`, `Unit`, `RecipeIngredient`
   - Business rules and domain validation

2. **Application Layer** - Use cases and workflows (✅ Complete)
   - Use Cases: `RegisterIngredientUseCase`, `RegisterRecipeUseCase`
   - DTOs: Organized into `command/` (input) and `entity/` (output)
   - Repository interfaces for dependency inversion
   - Entity factories for proper object creation

3. **Infrastructure Layer** - External concerns (🚧 Next to implement)
   - Database persistence with JPA/Hibernate
   - REST API controllers
   - External service integrations

### 🎯 Key Design Decisions

- **Domain-Driven Design**: Aggregates, value objects, and ubiquitous language
- **Dependency Inversion**: Application layer defines interfaces, infrastructure implements
- **DTO Separation**: Commands (input) and entities (output) in separate packages
- **Factory Pattern**: Centralized entity creation with proper validation
- **Test-Driven Development**: 55 comprehensive tests with high coverage

---

## License

MIT License © 2025
