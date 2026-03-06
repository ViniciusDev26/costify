# Costify - Architecture Overview

## Project Summary
Costify is a Java platform for calculating product costs from recipes using Clean Architecture principles. Each ingredient has quantity, unit, and price data to compute real recipe costs.

## Architecture Overview

### Clean Architecture Layers
The project follows Clean/Hexagonal Architecture with clear separation of concerns:

```
┌─────────────────────────────────────────────────────────────┐
│                    Presentation Layer                       │
│                   (Controllers, DTOs)                       │
│  🎯 OBJECTIVE: Handle HTTP requests, input validation,      │
│     response formatting, and API documentation             │
└─────────────────┬───────────────────────────────────────────┘
                  │
┌─────────────────┴───────────────────────────────────────────┐
│                  Application Layer                          │
│              (Use Cases, Services)                          │
│  🎯 OBJECTIVE: Orchestrate business workflows, coordinate   │
│     domain objects, and implement application-specific     │
│     business rules and use cases                           │
└─────────────────┬───────────────────────────────────────────┘
                  │
┌─────────────────┴───────────────────────────────────────────┐
│                    Domain Layer                             │
│        (Entities, Value Objects, Contracts)                │
│  🎯 OBJECTIVE: Contains core business logic, domain rules,  │
│     entities, and business invariants. Framework-agnostic  │
│     and represents the heart of the application            │
└─────────────────┬───────────────────────────────────────────┘
                  │
┌─────────────────┴───────────────────────────────────────────┐
│                Infrastructure Layer                         │
│       (Repositories, External APIs, Database)              │
│  🎯 OBJECTIVE: Implements technical capabilities like       │
│     database access, external APIs, file systems, and     │
│     framework-specific configurations                      │
└─────────────────────────────────────────────────────────────┘
```

#### Layer Responsibilities

**🌐 Presentation Layer**
- **Purpose**: User interface and external communication
- **Components**: REST controllers, request/response DTOs, input validation
- **Rules**: No business logic, only translation between external formats and application layer
- **Dependencies**: Depends on Application layer only

**⚙️ Application Layer**  
- **Purpose**: Application-specific business processes and coordination
- **Components**: Use case services, application DTOs, workflow orchestration
- **Rules**: Coordinates domain objects, handles transactions, manages application flow
- **Dependencies**: Depends on Domain layer, defines ports for Infrastructure

**💎 Domain Layer**
- **Purpose**: Core business logic and domain model
- **Components**: Entities, Value Objects, Domain Services, Business Rules
- **Rules**: Framework-agnostic, contains business invariants, no external dependencies
- **Dependencies**: No dependencies on other layers (innermost layer)

**🔧 Infrastructure Layer**
- **Purpose**: Technical implementation details and external systems
- **Components**: Database repositories, external API clients, file systems, messaging
- **Rules**: Implements ports defined by Application layer, contains framework-specific code
- **Dependencies**: Depends on Application and Domain layers

### Technology Stack
- **Java 21**
- **Spring Boot 3.5.5**
- **Maven** (build tool)
- **PostgreSQL** (database)
- **Flyway** (database migrations)
- **JUnit 5** (testing)
- **Testcontainers** (integration testing)
- **Lombok** (boilerplate reduction)
- **Spring Security** (authentication/authorization)

## Current Folder Structure

```
costify/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── br/unifor/costify/
│   │   │       ├── CostifyApplication.java          # Spring Boot main class
│   │   │       ├── application/                     # Application Layer (Use Cases & DTOs)
│   │   │       │   ├── contracts/
│   │   │       │   │   ├── IngredientRepository.java # Ingredient repository interface
│   │   │       │   │   └── RecipeRepository.java    # Recipe repository interface
│   │   │       │   ├── dto/
│   │   │       │   │   ├── command/                 # Input DTOs for commands
│   │   │       │   │   │   ├── RegisterIngredientCommand.java
│   │   │       │   │   │   ├── RegisterRecipeCommand.java
│   │   │       │   │   │   └── UpdateIngredientCommand.java
│   │   │       │   │   ├── entity/                  # Output DTOs for entities
│   │   │       │   │   │   ├── IngredientDto.java
│   │   │       │   │   │   └── RecipeDto.java
│   │   │       │   │   └── response/                # Response DTOs
│   │   │       │   │   │   ├── IngredientCostDto.java
│   │   │       │   │   │   ├── RecipeCostDto.java
│   │   │       │   │   │   └── UnitDto.java
│   │   │       │   ├── errors/                      # Application exceptions
│   │   │       │   │   ├── IngredientAlreadyExistsException.java
│   │   │       │   │   ├── IngredientNotFoundException.java
│   │   │       │   │   ├── RecipeAlreadyExistsException.java
│   │   │       │   │   └── RecipeNotFoundException.java
│   │   │       │   ├── config/
│   │   │       │   │   └── ValidationConfig.java     # Validation configuration
│   │   │       │   ├── events/                      # Application events (empty)
│   │   │       │   ├── service/
│   │   │       │   │   └── IngredientLoaderService.java # Ingredient loading service
│   │   │       │   ├── validation/
│   │   │       │   │   └── ValidationService.java   # Input validation service
│   │   │       │   ├── factory/
│   │   │       │   │   ├── IngredientFactory.java   # Ingredient creation factory
│   │   │       │   │   └── RecipeFactory.java       # Recipe creation factory
│   │   │       │   └── usecase/
│   │   │       │       ├── CalculateRecipeCostUseCase.java # Recipe cost calculation logic
│   │   │       │       ├── ListAvailableUnitsUseCase.java  # List available units logic
│   │   │       │       ├── ListIngredientsUseCase.java     # List ingredients logic
│   │   │       │       ├── RegisterIngredientUseCase.java  # Ingredient registration logic
│   │   │       │       ├── RegisterRecipeUseCase.java      # Recipe registration logic
│   │   │       │       └── UpdateIngredientUseCase.java    # Ingredient update logic
│   │   │       ├── domain/                          # Domain Layer (Core Business Logic)
│   │   │       │   ├── contracts/
│   │   │       │   │   └── IdGenerator.java         # Abstract ID generation
│   │   │       │   ├── entity/
│   │   │       │   │   ├── Ingredient.java          # Ingredient domain entity
│   │   │       │   │   └── Recipe.java              # Recipe domain entity
│   │   │       │   ├── errors/                      # Domain exceptions
│   │   │       │   │   ├── DomainException.java     # Base domain exception
│   │   │       │   │   ├── ingredient/
│   │   │       │   │   │   └── InvalidIngredientNameException.java
│   │   │       │   │   ├── money/
│   │   │       │   │   │   └── NegativeMoneyException.java
│   │   │       │   │   └── recipe/
│   │   │       │   │       ├── EmptyRecipeException.java
│   │   │       │   │       ├── InvalidQuantityException.java
│   │   │       │   │       └── InvalidTotalCostException.java
│   │   │       │   ├── events/                      # Domain events
│   │   │       │   │   ├── ingredient/              # Ingredient domain events (empty)
│   │   │       │   │   └── recipe/                  # Recipe domain events (empty)
│   │   │       │   ├── service/
│   │   │       │   │   └── RecipeCostCalculationService.java # Cost calculation domain service
│   │   │       │   └── valueobject/
│   │   │       │       ├── Id.java                  # Domain ID value object
│   │   │       │       ├── IngredientCost.java      # Ingredient cost value object
│   │   │       │       ├── Money.java               # Money value object
│   │   │       │       ├── RecipeCost.java          # Recipe cost value object
│   │   │       │       ├── RecipeIngredient.java    # Recipe-ingredient relationship
│   │   │       │       └── Unit.java                # Measurement unit value object
│   │   │       ├── infra/                           # Infrastructure Layer (Implemented)
│   │   │       │   ├── config/
│   │   │       │   │   ├── SecurityConfig.java      # Security configuration
│   │   │       │   │   └── UuidGenerator.java       # UUID generation implementation
│   │   │       │   ├── controllers/
│   │   │       │   │   ├── IngredientController.java # REST endpoints for ingredients
│   │   │       │   │   ├── RecipeController.java    # REST endpoints for recipes
│   │   │       │   │   ├── UnitController.java      # REST endpoints for units
│   │   │       │   │   └── dto/
│   │   │       │   │       ├── IngredientControllerRegisterRequest.java
│   │   │       │   │       ├── RecipeControllerRegisterRequest.java
│   │   │       │   │       └── RecipeControllerRegisterIngredientDto.java
│   │   │       │   └── data/
│   │   │       │       ├── entities/
│   │   │       │       │   ├── IngredientTable.java # JPA entity for ingredients
│   │   │       │       │   ├── RecipeTable.java     # JPA entity for recipes
│   │   │       │       │   └── RecipeIngredientTable.java # JPA entity for recipe-ingredient relationship
│   │   │       │       └── repositories/
│   │   │       │           ├── jpa/
│   │   │       │           │   ├── JpaIngredientRepository.java # JPA repository interface
│   │   │       │           │   └── JpaRecipeRepository.java # JPA recipe repository interface
│   │   │       │           └── postgres/
│   │   │       │               ├── PostgresIngredientRepository.java # Repository implementation
│   │   │       │               └── PostgresRecipeRepository.java # Recipe repository implementation
│   │   │       └── infrastructure/                  # Additional infrastructure (mostly empty)
│   │   │           └── events/                      # Infrastructure events (empty)
│   │   └── resources/
│   │       ├── application.properties               # Spring configuration
│   │       └── db/migration/                        # Flyway migrations (4 files)
│   │           ├── V1__Create_ingredients_and_recipes_tables.sql
│   │           ├── V2__Convert_unit_fields_to_enum.sql
│   │           ├── V3__Remove_unit_cost_column.sql
│   │           └── V4__Add_total_cost_column_to_recipes.sql
│   └── test/
│       └── java/
│           └── br/unifor/costify/
│               ├── CostifyApplicationTests.java     # Application context tests
│               ├── TestCostifyApplication.java     # Test configuration
│               ├── TestcontainersConfiguration.java # Testcontainers setup
│               ├── application/                     # Application layer tests
│               │   ├── dto/                         # DTO tests
│               │   │   ├── command/                 # Command DTO tests (empty)
│               │   │   ├── response/                # Response DTO tests (empty)
│               │   │   ├── IngredientDtoTest.java
│               │   │   ├── RecipeDtoTest.java
│               │   │   ├── RegisterIngredientCommandTest.java
│               │   │   └── RegisterRecipeCommandTest.java
│               │   └── usecase/                     # Use case tests
│               │       ├── CalculateRecipeCostUseCaseTest.java
│               │       ├── ListAvailableUnitsUseCaseTest.java
│               │       ├── ListIngredientsUseCaseTest.java
│               │       ├── RegisterIngredientUseCaseTest.java
│               │       └── RegisterRecipeUseCaseTest.java
│               ├── domain/                          # Domain unit tests
│               │   ├── entity/
│               │   │   ├── IngredientTest.java
│               │   │   └── RecipeTest.java
│               │   ├── events/                      # Domain event tests (empty)
│               │   ├── service/
│               │   │   └── RecipeCostCalculationServiceTest.java
│               │   └── valueobject/
│               │       ├── IdTest.java
│               │       ├── MoneyTest.java
│               │       ├── RecipeIngredientTest.java
│               │       └── UnitTest.java
│               └── integration/
│                   ├── flyway/
│                   │   └── FlywayMigrationIntegrationTest.java
│                   └── repository/
│                       ├── ingredient/
│                       │   ├── IngredientRepositoryConstraintsIntegrationTest.java
│                       │   └── PostgresIngredientRepositoryIntegrationTest.java
│                       └── recipe/
│                           ├── AdvancedRecipeRepositoryIntegrationTest.java
│                           ├── BasicRecipeRepositoryIntegrationTest.java
│                           └── RecipeRepositoryConstraintsIntegrationTest.java
├── target/                                          # Maven build output
├── docker-compose.yml                               # PostgreSQL container setup
├── pom.xml                                         # Maven configuration
├── mvnw                                            # Maven wrapper (Unix)
├── mvnw.cmd                                        # Maven wrapper (Windows)
├── CLAUDE.md                                       # Architecture documentation
└── README.md                                       # Project documentation
```

## Architecture Implementation Status

### ✅ Completed Layers

#### 1. Domain Layer (`src/main/java/br/unifor/costify/domain/`)

```text
domain/                                  # Core business logic (COMPLETED)
├── contracts/
│   └── IdGenerator.java                 # ID generation contract
├── entity/
│   ├── Ingredient.java                  # Ingredient aggregate root
│   └── Recipe.java                      # Recipe aggregate root
├── errors/                              # Domain exceptions (COMPLETED)
│   ├── DomainException.java             # Base domain exception
│   ├── ingredient/
│   │   └── InvalidIngredientNameException.java
│   ├── money/
│   │   └── NegativeMoneyException.java
│   └── recipe/
│       ├── EmptyRecipeException.java
│       ├── InvalidQuantityException.java
│       └── InvalidTotalCostException.java
├── events/                              # Domain events (structure ready)
│   ├── ingredient/
│   └── recipe/
├── service/
│   └── RecipeCostCalculationService.java # Cost calculation domain service
└── valueobject/
    ├── Id.java                          # Domain ID value object
    ├── IngredientCost.java              # Ingredient cost value object
    ├── Money.java                       # Money value object
    ├── RecipeCost.java                  # Recipe cost value object
    ├── RecipeIngredient.java            # Recipe-ingredient relationship
    └── Unit.java                        # Measurement unit enum
```

#### 2. Application Layer (`src/main/java/br/unifor/costify/application/`)

```text
application/                             # Business use cases (COMPLETED)
├── config/
│   └── ValidationConfig.java            # Validation configuration
├── contracts/
│   ├── IngredientRepository.java        # Repository interfaces
│   └── RecipeRepository.java
├── dto/
│   ├── command/                         # Input DTOs
│   │   ├── RegisterIngredientCommand.java
│   │   ├── RegisterRecipeCommand.java
│   │   └── UpdateIngredientCommand.java
│   ├── entity/                          # Output DTOs
│   │   ├── IngredientDto.java
│   │   └── RecipeDto.java
│   └── response/                        # Response DTOs
│       ├── IngredientCostDto.java
│       ├── RecipeCostDto.java
│       └── UnitDto.java
├── errors/                              # Application exceptions
│   ├── IngredientAlreadyExistsException.java
│   ├── IngredientNotFoundException.java
│   ├── RecipeAlreadyExistsException.java
│   └── RecipeNotFoundException.java
├── events/                              # Application events (empty)
├── factory/
│   ├── IngredientFactory.java           # Entity creation factories
│   └── RecipeFactory.java
├── service/
│   └── IngredientLoaderService.java     # Ingredient loading service
├── validation/
│   └── ValidationService.java           # Input validation service
└── usecase/
    ├── CalculateRecipeCostUseCase.java  # Recipe cost calculation logic
    ├── ListAvailableUnitsUseCase.java   # List available units workflow
    ├── ListIngredientsUseCase.java      # List ingredients workflow
    ├── RegisterIngredientUseCase.java   # Ingredient registration workflow
    ├── RegisterRecipeUseCase.java       # Recipe registration workflow
    └── UpdateIngredientUseCase.java     # Ingredient update workflow
```

#### 3. Infrastructure Layer (`src/main/java/br/unifor/costify/infra/`) - ✅ COMPLETED

```text
infra/                                   # Infrastructure implementation (COMPLETED)
├── config/
│   ├── SecurityConfig.java              # Security configuration
│   └── UuidGenerator.java               # UUID generation implementation
├── controllers/
│   ├── IngredientController.java        # REST endpoints for ingredients
│   ├── RecipeController.java            # REST endpoints for recipes
│   ├── UnitController.java              # REST endpoints for units
│   └── dto/
│       ├── IngredientControllerRegisterRequest.java
│       ├── RecipeControllerRegisterRequest.java
│       └── RecipeControllerRegisterIngredientDto.java
└── data/
    ├── entities/
    │   ├── IngredientTable.java         # JPA entity for ingredients
    │   ├── RecipeTable.java             # JPA entity for recipes
    │   └── RecipeIngredientTable.java   # JPA entity for recipe-ingredient relationship
    └── repositories/
        ├── jpa/
        │   ├── JpaIngredientRepository.java # JPA repository interface
        │   └── JpaRecipeRepository.java # JPA recipe repository interface
        └── postgres/
            ├── PostgresIngredientRepository.java # Repository implementation
            └── PostgresRecipeRepository.java # Recipe repository implementation
```

#### 4. Database Migrations (`src/main/resources/db/migration/`) - ✅ COMPLETED

```text
db/migration/
├── V1__Create_ingredients_and_recipes_tables.sql
├── V2__Convert_unit_fields_to_enum.sql
├── V3__Remove_unit_cost_column.sql
└── V4__Add_total_cost_column_to_recipes.sql
```

### ✅ Architecture Implementation Complete

The Costify application now has a complete Clean Architecture implementation with all core components:

#### ✅ Fully Implemented Features

- **Complete Recipe Management**: Full CRUD operations with REST endpoints
- **Complete Ingredient Management**: Full CRUD operations with REST endpoints
- **Unit Management**: List available measurement units
- **Recipe Cost Calculation**: Core business feature with cost breakdown
- **Repository Pattern**: Full database abstraction layer implemented
- **Domain-Driven Design**: Complete domain model with entities, value objects, and services
- **Comprehensive Testing**: Unit tests, integration tests, and repository tests
- **Database Migrations**: Complete schema evolution with 5 migrations

#### 🎯 Current Capabilities

1. **Recipe Operations**
   - Create recipes with multiple ingredients
   - Calculate total recipe costs automatically
   - Retrieve recipe details with cost breakdowns
   - Update recipe information

2. **Ingredient Operations**
   - Register ingredients with units and pricing
   - Update ingredient information
   - Retrieve ingredient details
   - List all registered ingredients
   - Cost calculations per unit

3. **Unit Operations**
   - List all available measurement units
   - View unit types (VOLUME, WEIGHT, UNIT)
   - View conversion factors to base units

4. **Cost Calculation Engine**
   - Real-time recipe cost calculation
   - Ingredient cost breakdowns
   - Unit conversion and pricing

### 🚀 Potential Enhancements (Future Considerations)

#### Advanced Features
- Recipe versioning and cost history tracking
- Bulk operations for ingredients and recipes
- Advanced cost analytics and reporting
- Recipe scaling and portion calculations

#### Technical Improvements
- API documentation (OpenAPI/Swagger integration)
- Caching layer for performance optimization
- Advanced error handling with detailed error codes
- Audit logging for changes
- Event-driven architecture for notifications

## Comprehensive Testing Strategy

The Costify application has extensive test coverage across all architectural layers:

### ✅ Test Coverage Overview

#### 1. Unit Tests (Domain & Application Layers)
- **Domain Entities**: Complete test coverage for `Ingredient` and `Recipe` entities
- **Value Objects**: Comprehensive tests for `Money`, `Unit`, `Id`, and `RecipeIngredient`
- **Use Cases**: Full test coverage for all business workflows
- **DTOs**: Complete validation and mapping tests for all data transfer objects

#### 2. Integration Tests (Infrastructure Layer)
- **Repository Tests**: Comprehensive database integration tests with Testcontainers
- **Database Constraints**: Complete constraint validation testing
- **Migration Tests**: Flyway migration verification tests

#### 3. Test Statistics
- **Total Test Files**: 24+ test classes
- **Total Tests**: 111+ test scenarios
- **Coverage Areas**: Domain (7 test classes), Application (10 test classes), Infrastructure (4 test classes), Integration (3 test classes)
- **Database Testing**: PostgreSQL integration with Testcontainers for realistic testing
- **Migration Testing**: Complete database schema evolution verification

### 🧪 Key Testing Features

#### Repository Integration Testing
```text
integration/repository/
├── ingredient/
│   ├── IngredientRepositoryConstraintsIntegrationTest.java
│   └── PostgresIngredientRepositoryIntegrationTest.java
└── recipe/
    ├── AdvancedRecipeRepositoryIntegrationTest.java
    ├── BasicRecipeRepositoryIntegrationTest.java
    └── RecipeRepositoryConstraintsIntegrationTest.java
```

#### Use Case Testing
```text
application/usecase/
├── CalculateRecipeCostUseCaseTest.java
├── ListAvailableUnitsUseCaseTest.java
├── ListIngredientsUseCaseTest.java
├── RegisterIngredientUseCaseTest.java
└── RegisterRecipeUseCaseTest.java
```

#### Controller Testing
```text
infra/controllers/
├── IngredientControllerTest.java
└── UnitControllerTest.java
```

#### Domain Model Testing
```text
domain/
├── entity/
│   ├── IngredientTest.java
│   └── RecipeTest.java
├── service/
│   └── RecipeCostCalculationServiceTest.java
└── valueobject/
    ├── IdTest.java
    ├── MoneyTest.java
    ├── RecipeIngredientTest.java
    └── UnitTest.java
```

## Build & Development Commands

**IMPORTANT: This project runs exclusively via Docker.**

### Essential Docker Commands
```bash
# Start application (builds automatically if needed)
docker-compose up -d

# View logs
docker-compose logs -f app

# Rebuild after code changes
docker-compose up -d --build app

# Stop application
docker-compose stop

# Remove containers and data
docker-compose down -v

# Check status
docker-compose ps
```

### Running Tests
```bash
# Run all tests inside container
docker-compose exec app ./mvnw test -DargLine="-ea"

# Run specific test class
docker-compose exec app ./mvnw test -Dtest=IngredientTest

# Run integration tests only
docker-compose exec app ./mvnw test -Dtest="**/*IntegrationTest"
```

### Development Workflow
1. **Start containers**: `docker-compose up -d`
2. **View logs**: `docker-compose logs -f app`
3. **Make code changes**: Edit files locally
4. **Rebuild**: `docker-compose up -d --build app`
5. **Run tests**: `docker-compose exec app ./mvnw test -DargLine="-ea"`
6. **Database migrations**: Applied automatically via Flyway on startup

## REST API Endpoints

The application exposes the following REST API endpoints at `/api`:

### Ingredient Endpoints

#### Create Ingredient
```http
POST /api/ingredients
Content-Type: application/json

{
  "name": "Milk",
  "packageQuantity": 1.0,
  "packagePrice": 5.50,
  "packageUnit": "L"
}
```
**Response**: `IngredientDto` with calculated unit cost

#### List All Ingredients
```http
GET /api/ingredients
```
**Response**: Array of `IngredientDto` objects
```json
[
  {
    "id": "uuid",
    "name": "Milk",
    "packageQuantity": 1.0,
    "packagePrice": 5.50,
    "packageUnit": "L",
    "unitCost": 0.0055
  }
]
```

### Recipe Endpoints

#### Create Recipe
```http
POST /api/recipes
Content-Type: application/json

{
  "name": "Cake",
  "ingredients": [
    {
      "ingredientId": "ingredient-uuid",
      "quantity": 500.0,
      "unit": "ML"
    }
  ]
}
```
**Response**: `RecipeDto` with total cost

#### Calculate Recipe Cost
```http
GET /api/recipes/{recipeId}/cost
```
**Response**: `RecipeCostDto` with detailed cost breakdown

### Unit Endpoints

#### List Available Units
```http
GET /api/units
```
**Response**: Array of available measurement units
```json
[
  {
    "name": "ML",
    "type": "VOLUME",
    "factorToBase": 1.0
  },
  {
    "name": "L",
    "type": "VOLUME",
    "factorToBase": 1000.0
  },
  {
    "name": "G",
    "type": "WEIGHT",
    "factorToBase": 1.0
  },
  {
    "name": "KG",
    "type": "WEIGHT",
    "factorToBase": 1000.0
  },
  {
    "name": "TBSP",
    "type": "VOLUME",
    "factorToBase": 15.0
  },
  {
    "name": "TBSP_BUTTER",
    "type": "WEIGHT",
    "factorToBase": 14.0
  },
  {
    "name": "UN",
    "type": "UNIT",
    "factorToBase": 1.0
  }
]
```

**Available Units**:
- **VOLUME**: ML (milliliter), L (liter), TBSP (tablespoon)
- **WEIGHT**: G (gram), KG (kilogram), TBSP_BUTTER (tablespoon of butter)
- **UNIT**: UN (unit/piece)

## Key Design Principles

### Domain-Driven Design
- **Entities**: `Ingredient` and `Recipe` aggregates with business identity and behavior
- **Value Objects**: `Id`, `Unit`, `RecipeIngredient` for immutable domain concepts
- **Contracts**: `IdGenerator` for dependency inversion
- **Repository Interfaces**: `IngredientRepository`, `RecipeRepository` for data persistence abstraction
- **Use Cases**: `RegisterIngredientUseCase`, `RegisterRecipeUseCase` for application workflows

### Clean Architecture Benefits
- **Independence**: Domain logic isolated from frameworks
- **Testability**: Each layer can be tested independently  
- **Maintainability**: Clear separation of concerns
- **Flexibility**: Easy to swap infrastructure components

## Claude Development Protocol

### 📋 Mandatory Pre-Execution Rules
**BEFORE ANY CODE IMPLEMENTATION**, Claude must:

1. **📝 Create TodoWrite Plan**
   - Break down task into specific, testable steps
   - Include TDD cycle for each component
   - Mark one task as `in_progress` at a time

2. **🔒 Git Safety Checkpoint**
   ```bash
   git add . && git commit -m "checkpoint: before [task-name]" --allow-empty
   ```
   - Always commit current state before starting
   - Provides rollback point if needed
   - **IMPORTANT**: This checkpoint will be squashed with final commit

3. **🧪 TDD Development Cycle**
   - **RED**: Write failing test first
   - **GREEN**: Write minimal code to pass
   - **REFACTOR**: Improve code quality
   - Repeat for each component

### 🎯 Standard Implementation Flow

#### Phase 1: Planning & Safety
```bash
# 1. Save current state
git add . && git commit -m "checkpoint: before implementing [feature]"

# 2. Verify environment (start containers)
docker-compose up -d

# 3. Run existing tests to verify baseline
docker-compose exec app ./mvnw test -DargLine="-ea"
```

#### Phase 2: TDD Implementation
For each component (Entity, Service, Controller):

1. **RED Phase**
   ```bash
   # Write failing test
   docker-compose exec app ./mvnw test -Dtest=ComponentNameTest
   # Expected: Test fails (RED)
   ```

2. **GREEN Phase**
   ```bash
   # Write minimal implementation
   docker-compose exec app ./mvnw test -Dtest=ComponentNameTest
   # Expected: Test passes (GREEN)
   ```

3. **REFACTOR Phase**
   ```bash
   # Improve code quality
   docker-compose exec app ./mvnw test  # All tests still pass
   ```

#### Phase 3: Integration & Validation
```bash
# Rebuild application with changes
docker-compose up -d --build app

# Full test suite
docker-compose exec app ./mvnw clean test -DargLine="-ea"

# Verify application is running
curl http://localhost:8080/api/actuator/health

# Final squash commit (combines checkpoint + implementation)
git reset --soft HEAD~1  # Reset to before checkpoint but keep changes
git add . && git commit -m "✅ implement [feature] with TDD

- Detailed description of what was implemented
- Key features and improvements
- TDD approach followed

🎯 Business value delivered

🧪 Generated with [Claude Code](https://claude.ai/code)

Co-Authored-By: Claude <noreply@anthropic.com>"
```

### 🔄 Rollback Strategy
If implementation fails or breaks existing functionality:
```bash
# View recent commits
git log --oneline -5

# Rollback to checkpoint
git reset --hard [checkpoint-commit-hash]

# Or soft rollback (keep changes)
git reset --soft [checkpoint-commit-hash]
```

### 🎯 Squash Commit Strategy
To maintain clean git history with one commit per feature:

```bash
# After successful implementation, squash checkpoint + implementation
git reset --soft HEAD~1  # Reset to before checkpoint, keep all changes staged
git add . && git commit -m "✅ implement [feature] with comprehensive description"

# This results in ONE commit instead of:
# - checkpoint: before [feature] 
# - ✅ implement [feature]
```

**Benefits:**
- Clean, linear git history
- Each commit represents a complete feature
- Easy to review and revert if needed
- Professional commit history for production

### 📋 TodoWrite Template
Every task must start with this structure:
```
1. Create git checkpoint - pending
2. Write failing test for [component] - pending  
3. Implement minimal [component] code - pending
4. Refactor [component] for quality - pending
5. Integration test verification - pending
6. Final squash commit and validation - pending
```

### 🎯 TDD Success Criteria
- [ ] Every public method has a test
- [ ] Tests written before implementation
- [ ] All tests pass consistently
- [ ] Code coverage maintained
- [ ] Git history shows TDD progression
- [ ] Rollback checkpoint available

## Future Enhancements

### Core Business Features
- Recipe cost calculation algorithms with ingredient breakdown
- Recipe management with complete CRUD operations
- Ingredient price history tracking
- Recipe versioning and cost comparison

### Technical Improvements
- API documentation with OpenAPI/Swagger
- Caching layer for performance optimization
- Event-driven architecture for scalability
- Advanced validation and error handling
- Audit logging for ingredient/recipe changes

### Quality & Testing
- Comprehensive integration test coverage
- Performance testing for cost calculations
- Contract testing for API compatibility
- Load testing for concurrent recipe calculations