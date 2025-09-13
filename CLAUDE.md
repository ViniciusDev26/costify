# Costify - Architecture Overview

## Project Summary
Costify is a TypeScript platform for calculating product costs from recipes using Clean Architecture principles. Built with Bun + Elysia for high performance and Decimal.js for precise financial calculations. Each ingredient has quantity, unit, and price data to compute real recipe costs.

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
- **Bun** (JavaScript runtime)
- **TypeScript** (type-safe JavaScript)
- **Elysia** (fast and lightweight HTTP framework)
- **Drizzle ORM** (TypeScript ORM with SQL-like syntax)
- **PostgreSQL** (database)
- **Decimal.js** (precise financial calculations)
- **Zod** (schema validation)
- **Testcontainers** (integration testing)
- **Biome** (linting and formatting)

## Current Folder Structure

```
costify/
├── src/
│   ├── main.ts                                     # Application entry point (Bun + Elysia)
│   ├── application/                                # Application Layer (Use Cases & DTOs)
│   │   ├── contracts/
│   │   │   ├── IngredientRepository.ts             # Ingredient repository interface
│   │   │   └── RecipeRepository.ts                 # Recipe repository interface
│   │   ├── dto/
│   │   │   ├── command/                            # Input DTOs for commands
│   │   │   │   ├── RegisterIngredientCommand.ts
│   │   │   │   ├── RegisterRecipeCommand.ts
│   │   │   │   ├── UpdateIngredientCommand.ts
│   │   │   │   └── CalculateRecipeCostCommand.ts
│   │   │   ├── entity/                             # Output DTOs for entities
│   │   │   │   ├── IngredientDto.ts
│   │   │   │   └── RecipeDto.ts
│   │   │   └── response/                           # Response DTOs
│   │   │       ├── IngredientCostDto.ts
│   │   │       └── RecipeCostDto.ts
│   │   ├── errors/                                 # Application exceptions
│   │   │   ├── ApplicationException.ts
│   │   │   ├── IngredientAlreadyExistsException.ts
│   │   │   ├── IngredientNotFoundException.ts
│   │   │   ├── RecipeAlreadyExistsException.ts
│   │   │   └── RecipeNotFoundException.ts
│   │   ├── factory/
│   │   │   ├── IngredientFactory.ts                # Ingredient creation factory
│   │   │   └── RecipeFactory.ts                    # Recipe creation factory
│   │   ├── mapper/
│   │   │   ├── IngredientMapper.ts                 # Domain <-> DTO mapping
│   │   │   └── RecipeMapper.ts                     # Domain <-> DTO mapping
│   │   └── usecase/
│   │       ├── CalculateRecipeCostUseCase.ts       # Recipe cost calculation logic
│   │       ├── RegisterIngredientUseCase.ts        # Ingredient registration logic
│   │       ├── RegisterRecipeUseCase.ts            # Recipe registration logic
│   │       └── UpdateIngredientUseCase.ts          # Ingredient update logic
│   ├── domain/                                     # Domain Layer (Core Business Logic)
│   │   ├── contracts/
│   │   │   └── IdGenerator.ts                      # Abstract ID generation
│   │   ├── entities/
│   │   │   ├── Ingredient.ts                       # Ingredient domain entity
│   │   │   └── Recipe.ts                           # Recipe domain entity
│   │   ├── errors/                                 # Domain exceptions
│   │   │   ├── DomainException.ts                  # Base domain exception
│   │   │   ├── ingredient/
│   │   │   │   └── InvalidIngredientNameException.ts
│   │   │   ├── money/
│   │   │   │   └── NegativeMoneyException.ts
│   │   │   └── recipe/
│   │   │       ├── EmptyRecipeException.ts
│   │   │       ├── InvalidQuantityException.ts
│   │   │       └── InvalidTotalCostException.ts
│   │   ├── services/
│   │   │   └── RecipeCostCalculationService.ts     # Cost calculation domain service
│   │   └── valueobjects/
│   │       ├── Id.ts                               # Domain ID value object
│   │       ├── IngredientCost.ts                   # Ingredient cost value object
│   │       ├── Money.ts                            # Money value object (Decimal.js)
│   │       ├── RecipeCost.ts                       # Recipe cost value object
│   │       ├── RecipeIngredient.ts                 # Recipe-ingredient relationship
│   │       └── Unit.ts                             # Measurement unit enum
│   └── infrastructure/                             # Infrastructure Layer
│       ├── config/
│       │   ├── database.ts                         # Drizzle database configuration
│       │   └── dependencies.ts                     # Dependency injection setup
│       ├── controllers/
│       │   ├── IngredientController.ts             # REST endpoints for ingredients
│       │   └── RecipeController.ts                 # REST endpoints for recipes
│       ├── database/
│       │   ├── migrate.ts                          # Database migration runner
│       │   ├── migrations/                         # Drizzle migrations
│       │   │   ├── meta/
│       │   │   └── [timestamp]_*.sql               # SQL migration files
│       │   └── schema/
│       │       ├── enums/
│       │       │   └── unit.ts                     # Unit enum definition
│       │       └── tables/
│       │           ├── ingredients.ts              # Ingredients table schema
│       │           ├── recipes.ts                  # Recipes table schema
│       │           └── recipe-ingredients.ts       # Recipe-ingredients join table
│       ├── mappers/
│       │   ├── IngredientDbMapper.ts               # Domain <-> DB mapping
│       │   └── RecipeDbMapper.ts                   # Domain <-> DB mapping
│       ├── middleware/
│       │   ├── cors.ts                             # CORS configuration
│       │   └── error-handler.ts                    # Global error handling
│       ├── providers/
│       │   ├── DatabaseProvider.ts                 # Database connection provider
│       │   └── UuidGenerator.ts                    # UUID generation implementation
│       └── repositories/
│           ├── DrizzleIngredientRepository.ts      # Ingredient repository implementation
│           └── DrizzleRecipeRepository.ts          # Recipe repository implementation
├── test/
│   ├── unit/                                       # Unit tests
│   │   ├── application/
│   │   │   └── usecase/
│   │   │       └── RegisterIngredientUseCase.test.ts
│   │   └── domain/
│   │       ├── entities/
│   │       │   └── Ingredient.test.ts
│   │       └── valueobjects/
│   │           └── Money.test.ts
│   └── integration/                                # Integration tests
│       └── repositories/
│           ├── DrizzleIngredientRepository.integration.test.ts
│           └── DrizzleRecipeRepository.integration.test.ts
├── drizzle.config.ts                               # Drizzle ORM configuration
├── docker-compose.yml                              # PostgreSQL container setup
├── package.json                                    # NPM/Bun configuration
├── bun.lockb                                       # Bun lock file
├── tsconfig.json                                   # TypeScript configuration
├── biome.json                                      # Biome linter/formatter config
├── CLAUDE.md                                       # Architecture documentation
└── readme.md                                       # Project documentation
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
│       └── RecipeCostDto.java
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
    ├── RegisterIngredientUseCase.java   # Business workflows
    ├── RegisterRecipeUseCase.java
    └── UpdateIngredientUseCase.java
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
- **Recipe Cost Calculation**: Core business feature with cost breakdown
- **Repository Pattern**: Full database abstraction layer implemented
- **Domain-Driven Design**: Complete domain model with entities, value objects, and services
- **Comprehensive Testing**: Unit tests, integration tests, and repository tests
- **Database Migrations**: Complete schema evolution with 4 migrations

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
   - Cost calculations per unit

3. **Cost Calculation Engine**
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

The Costify application has extensive test coverage across all architectural layers using **Bun Test** framework:

### ✅ Test Coverage Overview

#### 1. Unit Tests (Domain & Application Layers)
- **Domain Entities**: Complete test coverage for `Ingredient` and `Recipe` entities
- **Value Objects**: Comprehensive tests for `Money` value object with Decimal.js integration
- **Use Cases**: Full test coverage for business workflows including `RegisterIngredientUseCase`
- **Financial Operations**: Rigorous testing of monetary calculations to prevent floating-point errors

#### 2. Integration Tests (Infrastructure Layer)
- **Repository Tests**: Comprehensive database integration tests with real PostgreSQL
- **Database Operations**: Full CRUD operations testing for both ingredients and recipes
- **Data Integrity**: Complete constraint validation and cascade delete testing
- **Connection Management**: Proper database connection lifecycle management

#### 3. Test Statistics
- **Total Test Files**: 5 comprehensive test suites
- **Coverage Areas**: 
  - Unit Tests: Domain value objects and application use cases
  - Integration Tests: Database repositories with real PostgreSQL
- **Database Testing**: Direct PostgreSQL integration for realistic testing scenarios
- **Financial Precision**: Specialized tests for Decimal.js monetary calculations

### 🧪 Key Testing Features

#### Repository Integration Testing
```text
test/integration/repositories/
├── DrizzleIngredientRepository.integration.test.ts
└── DrizzleRecipeRepository.integration.test.ts
```

**Features:**
- Real PostgreSQL database connections
- Complete CRUD operations testing
- Data integrity and constraint validation
- Proper cleanup between tests
- Connection lifecycle management

#### Use Case Testing
```text
test/unit/application/usecase/
└── RegisterIngredientUseCase.test.ts
```

**Features:**
- Business workflow validation
- Error handling scenarios
- Input validation testing
- Repository integration mocking

#### Domain Model Testing
```text
test/unit/domain/
├── entities/
│   └── Ingredient.test.ts
└── valueobjects/
    └── Money.test.ts
```

**Features:**
- **Money Value Object**: Comprehensive testing of Decimal.js integration
  - Creation and validation
  - Arithmetic operations (add, subtract, multiply, divide)
  - Comparison operations
  - Error handling for negative amounts and division by zero
- **Ingredient Entity**: Business logic and invariant validation
- **Domain Rules**: Enforcement of business constraints

## Build & Development Commands

### Essential Bun Commands
```bash
# Development
bun dev                    # Start development server with hot reload
bun start                  # Start production server
bun build                  # Build for production

# Database
bun db:generate           # Generate Drizzle client types
bun db:migrate            # Run database migrations
bun db:push               # Push schema changes to database
bun db:studio             # Open Drizzle Studio

# Testing
bun test                  # Run all tests (unit + integration)
bun test:unit             # Run unit tests only
bun test:integration      # Run integration tests only
bun test:watch            # Run tests in watch mode

# Code Quality
bun lint                  # Run linter (Biome)
bun lint:fix              # Fix linting issues automatically
bun check                 # Run all checks (lint + format)
```

### Testing Setup and Environment

#### Prerequisites for Integration Tests
```bash
# Start PostgreSQL database
docker compose up -d

# Run database migrations
bun db:migrate

# Run all tests
bun test
```

#### Test Environment Configuration
- **Unit Tests**: No external dependencies, pure domain/application logic
- **Integration Tests**: Require PostgreSQL database connection
- **Database URL**: Uses `DATABASE_URL` environment variable or defaults to local PostgreSQL
- **Test Isolation**: Each integration test cleans up its data to prevent interference
- **Decimal.js Provider**: Money value object configured with DecimalJsProvider for precise calculations

### Development Workflow
1. **Database**: Ensure PostgreSQL is running locally with Docker Compose
2. **Migrations**: Use Drizzle Kit for schema management and evolution
3. **Testing**: 
   - Unit tests for domain logic and value objects
   - Integration tests with real PostgreSQL database
   - Financial calculation tests ensuring decimal precision
4. **Hot Reload**: Automatic restart on file changes during development
5. **Code Quality**: Biome for consistent linting and formatting

## Database Schema

The Costify application uses PostgreSQL with the following schema:

### Tables

#### `ingredients`
```sql
CREATE TABLE ingredients (
  id uuid PRIMARY KEY DEFAULT gen_random_uuid(),
  name varchar(255) NOT NULL UNIQUE,
  price_per_unit numeric(10, 2) NOT NULL,
  unit unit NOT NULL,
  created_at timestamp DEFAULT now() NOT NULL,
  updated_at timestamp DEFAULT now() NOT NULL
);
```

#### `recipes`
```sql
CREATE TABLE recipes (
  id uuid PRIMARY KEY DEFAULT gen_random_uuid(),
  name varchar(255) NOT NULL UNIQUE,
  total_cost numeric(10, 2) NOT NULL,
  created_at timestamp DEFAULT now() NOT NULL,
  updated_at timestamp DEFAULT now() NOT NULL
);
```

#### `recipe_ingredients`
```sql
CREATE TABLE recipe_ingredients (
  id uuid PRIMARY KEY DEFAULT gen_random_uuid(),
  recipe_id uuid NOT NULL REFERENCES recipes(id) ON DELETE CASCADE,
  ingredient_id uuid NOT NULL REFERENCES ingredients(id) ON DELETE CASCADE,
  quantity numeric(10, 4) NOT NULL,
  unit unit NOT NULL,
  created_at timestamp DEFAULT now() NOT NULL
);
```

### Enums

#### `unit`
```sql
CREATE TYPE unit AS ENUM(
  'GRAM', 'KILOGRAM', 'MILLILITER', 'LITER', 'PIECE', 
  'TABLESPOON', 'TEASPOON', 'CUP', 'OUNCE', 'POUND', 
  'TBSP', 'TBSP_BUTTER', 'UNIT'
);
```

### Relationships

- `recipes` has many `recipe_ingredients` (one-to-many)
- `ingredients` has many `recipe_ingredients` (one-to-many)
- `recipe_ingredients` belongs to one `recipe` and one `ingredient` (many-to-one)

### Constraints

- **Unique Names**: Both ingredients and recipes must have unique names
- **Cascade Deletes**: Deleting a recipe or ingredient cascades to related `recipe_ingredients`
- **Not Null**: All price and quantity fields are required
- **Precision**: Prices use `numeric(10,2)` and quantities use `numeric(10,4)` for exact calculations

## Key Design Principles

### Domain-Driven Design
- **Entities**: `Ingredient` and `Recipe` aggregates with business identity and behavior
- **Value Objects**: `Id`, `Money`, `Unit`, `RecipeIngredient` for immutable domain concepts
- **Contracts**: `IdGenerator` for dependency inversion
- **Repository Interfaces**: `IngredientRepository`, `RecipeRepository` for data persistence abstraction
- **Use Cases**: `RegisterIngredientUseCase`, `RegisterRecipeUseCase`, `CalculateRecipeCostUseCase` for application workflows

### Financial Precision
- **Decimal.js Integration**: All monetary calculations use Decimal.js through the `Money` value object
- **No Floating Point Errors**: Ensures accurate financial calculations for recipe costing
- **Type Safety**: TypeScript ensures compile-time validation of monetary operations
- **Immutability**: Money value objects are immutable, preventing accidental modifications

### Clean Architecture Benefits
- **Independence**: Domain logic isolated from frameworks (Elysia, Drizzle)
- **Testability**: Each layer can be tested independently with mocks and Testcontainers
- **Maintainability**: Clear separation of concerns across layers
- **Flexibility**: Easy to swap infrastructure components (e.g., database, web framework)
- **Type Safety**: Full TypeScript coverage ensures compile-time error detection
- **Performance**: Bun runtime provides exceptional performance for I/O operations

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

# 2. Verify environment
docker-compose up -d postgres
./mvnw test -DargLine="-ea"
```

#### Phase 2: TDD Implementation
For each component (Entity, Service, Controller):

1. **RED Phase**
   ```bash
   # Write failing test
   ./mvnw test -Dtest=ComponentNameTest
   # Expected: Test fails (RED)
   ```

2. **GREEN Phase**
   ```bash
   # Write minimal implementation
   ./mvnw test -Dtest=ComponentNameTest  
   # Expected: Test passes (GREEN)
   ```

3. **REFACTOR Phase**
   ```bash
   # Improve code quality
   ./mvnw test  # All tests still pass
   ```

#### Phase 3: Integration & Validation
```bash
# Full test suite
./mvnw clean test -DargLine="-ea"

# Application startup test
./mvnw spring-boot:run

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