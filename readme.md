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

Once the application is running, the following endpoints are available:

```bash
# Register new ingredient
POST /ingredients
Content-Type: application/json
{
  "name": "Flour", 
  "packageQuantity": 1.0,
  "packagePrice": 2.50,
  "packageUnit": "KG"
}

# Register new recipe with ingredients  
POST /recipes
Content-Type: application/json
{
  "name": "Bread",
  "ingredients": [
    {
      "ingredientId": "ingredient-uuid",
      "quantity": 0.5,
      "unit": "KG" 
    }
  ]
}
```

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
