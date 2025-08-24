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
│   │   │       └── domain/                          # Domain Layer (Core Business Logic)
│   │   │           ├── contracts/
│   │   │           │   └── IdGenerator.java         # Abstract ID generation
│   │   │           ├── entity/
│   │   │           │   └── Ingredient.java          # Core business entity
│   │   │           └── valueobject/
│   │   │               ├── Id.java                  # Domain ID value object
│   │   │               └── Unit.java               # Measurement unit value object
│   │   └── resources/
│   │       ├── application.properties               # Spring configuration
│   │       └── db/migration/                        # Flyway migrations (empty)
│   └── test/
│       └── java/
│           └── br/unifor/costify/
│               ├── CostifyApplicationTests.java     # Application context tests
│               ├── TestCostifyApplication.java     # Test configuration
│               ├── TestcontainersConfiguration.java # Testcontainers setup
│               ├── domain/                          # Domain unit tests
│               │   ├── entity/
│               │   │   └── IngredientTest.java
│               │   └── valueobject/
│               │       ├── IdTest.java
│               │       └── UnitTest.java
│               └── integration/
│                   └── flyway/
│                       └── FlywayMigrationIntegrationTest.java
├── target/                                          # Maven build output
├── pom.xml                                         # Maven configuration
├── mvnw                                            # Maven wrapper (Unix)
├── mvnw.cmd                                        # Maven wrapper (Windows)
└── README.md                                       # Project documentation
```

## Planned Architecture Implementation

### Missing Layers to Implement

#### 1. Application Layer (`src/main/java/br/unifor/costify/application/`)
```
application/
├── service/
│   ├── IngredientService.java           # Business use cases
│   └── CostCalculationService.java      # Cost calculation logic
├── dto/
│   ├── IngredientDto.java              # Data transfer objects
│   └── RecipeDto.java
└── port/
    ├── IngredientRepository.java        # Repository interfaces
    └── RecipeRepository.java
```

#### 2. Infrastructure Layer (`src/main/java/br/unifor/costify/infrastructure/`)
```
infrastructure/
├── persistence/
│   ├── entity/
│   │   ├── IngredientJpaEntity.java    # JPA entities
│   │   └── RecipeJpaEntity.java
│   ├── repository/
│   │   ├── IngredientJpaRepository.java # JPA repositories
│   │   └── RecipeJpaRepository.java
│   └── mapper/
│       └── IngredientMapper.java       # Entity <-> Domain mapping
├── web/
│   ├── controller/
│   │   ├── IngredientController.java   # REST endpoints
│   │   └── RecipeController.java
│   └── config/
│       ├── WebConfig.java              # Web configuration
│       └── SecurityConfig.java         # Security configuration
└── external/
    └── IdGeneratorImpl.java            # External ID generation
```

#### 3. Database Migrations (`src/main/resources/db/migration/`)
```
db/migration/
├── V1__Create_ingredients_table.sql
├── V2__Create_recipes_table.sql
└── V3__Create_recipe_ingredients_table.sql
```

## Build & Development Commands

### Essential Maven Commands
```bash
# Clean build
./mvnw clean compile

# Run application
./mvnw spring-boot:run

# Run tests (with Java assertions)
./mvnw test -DargLine="-ea"

# Full build with tests
./mvnw clean install

# Run specific test class
./mvnw test -Dtest=IngredientTest

# Run integration tests only
./mvnw test -Dtest="**/*IntegrationTest"
```

### Development Workflow
1. **Database**: Ensure PostgreSQL is running locally
2. **Migrations**: Create and run Flyway migrations
3. **Testing**: Use `-DargLine="-ea"` to enable Java assertions
4. **Integration Tests**: Use Testcontainers for database testing

## Key Design Principles

### Domain-Driven Design
- **Entities**: `Ingredient` with business identity and behavior
- **Value Objects**: `Id`, `Unit` for immutable domain concepts
- **Contracts**: `IdGenerator` for dependency inversion

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

# Final commit
git add . && git commit -m "✅ implement [feature] with TDD"
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

### 📋 TodoWrite Template
Every task must start with this structure:
```
1. Create git checkpoint - pending
2. Write failing test for [component] - pending  
3. Implement minimal [component] code - pending
4. Refactor [component] for quality - pending
5. Integration test verification - pending
6. Final commit and validation - pending
```

### 🎯 TDD Success Criteria
- [ ] Every public method has a test
- [ ] Tests written before implementation
- [ ] All tests pass consistently
- [ ] Code coverage maintained
- [ ] Git history shows TDD progression
- [ ] Rollback checkpoint available

## Future Enhancements
- Recipe management with multiple ingredients
- Cost calculation algorithms  
- API documentation with OpenAPI/Swagger
- Caching layer for performance
- Event-driven architecture for scalability