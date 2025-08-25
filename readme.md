# Costify

Costify is a Java platform to calculate product costs from recipes. Each ingredient has quantity, unit, and price, allowing the real cost of the recipe to be computed. Built with Clean Architecture and Spring Boot.

---

## Features

* **Clean Architecture** with Domain, Application, and Infrastructure layers
* **Domain Modeling** with Value Objects (`Id`, `Unit`, `RecipeIngredient`) and entities (`Ingredient`, `Recipe`)
* **Use Cases** for ingredient and recipe registration with business validation
* **Repository Pattern** with dependency inversion (`IngredientRepository`, `RecipeRepository`)
* **Entity Factories** for proper domain object creation
* **DTO Organization** with separate command and entity DTOs
* **Comprehensive Testing** with 55 unit and integration tests using JUnit 5
* **Database Integration** with PostgreSQL, Flyway migrations, and Testcontainers

---

## Technology Stack

* **Java 21** - Programming language
* **Spring Boot 3.5.5** - Application framework
* **Maven** - Build and dependency management
* **PostgreSQL** - Database
* **Flyway** - Database migration management
* **JUnit 5** - Unit testing framework
* **Mockito** - Mocking framework for tests
* **Testcontainers** - Integration testing with real database
* **Clean Architecture** - Architectural pattern
* **Domain-Driven Design** - Domain modeling approach

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
```

This enables Java assertions for domain validation tests. All 55 tests should pass.

---

## Project Structure

```
src/
├── main/
│   ├── java/br/unifor/costify/
│   │   ├── CostifyApplication.java          # Spring Boot main class
│   │   ├── application/                     # Application Layer (Use Cases & DTOs)
│   │   │   ├── contracts/                   # Repository interfaces
│   │   │   │   ├── IngredientRepository.java
│   │   │   │   └── RecipeRepository.java
│   │   │   ├── dto/
│   │   │   │   ├── command/                 # Input DTOs for commands
│   │   │   │   │   ├── RegisterIngredientCommand.java
│   │   │   │   │   └── RegisterRecipeCommand.java
│   │   │   │   └── entity/                  # Output DTOs for entities
│   │   │   │       ├── IngredientDto.java
│   │   │   │       └── RecipeDto.java
│   │   │   ├── factory/                     # Entity creation factories
│   │   │   │   ├── IngredientFactory.java
│   │   │   │   └── RecipeFactory.java
│   │   │   └── usecase/                     # Business use cases
│   │   │       ├── RegisterIngredientUseCase.java
│   │   │       └── RegisterRecipeUseCase.java
│   │   └── domain/                          # Domain Layer (Core Business Logic)
│   │       ├── contracts/
│   │       │   └── IdGenerator.java         # Abstract ID generation
│   │       ├── entity/
│   │       │   ├── Ingredient.java          # Ingredient aggregate root
│   │       │   └── Recipe.java              # Recipe aggregate root
│   │       └── valueobject/
│   │           ├── Id.java                  # Domain ID value object
│   │           ├── RecipeIngredient.java    # Recipe-ingredient relationship
│   │           └── Unit.java                # Measurement unit enum
│   └── resources/
│       ├── application.properties           # Spring configuration
│       └── db/migration/                    # Flyway migrations
└── test/
    └── java/br/unifor/costify/
        ├── application/                     # Application layer tests
        │   ├── dto/                         # DTO validation tests
        │   └── usecase/                     # Use case business logic tests
        ├── domain/                          # Domain unit tests
        │   ├── entity/                      # Entity behavior tests
        │   └── valueobject/                 # Value object tests
        └── integration/                     # Integration tests
            └── flyway/                      # Database migration tests
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
