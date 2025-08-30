# Costify - Full Stack Architecture Overview

## Project Summary
Costify is a modern full-stack application for calculating product costs from recipes using Clean Architecture principles. It features a Spring Boot API backend with Angular frontend, designed for professional recipe cost analysis. Each ingredient has quantity, unit, and price data to compute accurate real-world recipe costs.

### 🎯 Core Value Proposition
- **Recipe Cost Analysis**: Calculate precise ingredient-level costs for recipes
- **Professional Kitchen Management**: Built for restaurants, bakeries, and food businesses
- **Clean Architecture**: Maintainable, testable, and scalable codebase
- **Modern Tech Stack**: Spring Boot 3.5 + Angular 20 + PostgreSQL

## Full Stack Architecture Overview

### System Architecture
Costify implements a modern full-stack architecture with clean separation between frontend, backend, and database layers:

```
┌─────────────────────────────────────────────────────────────┐
│                 FRONTEND LAYER                              │
│              Angular 20 Application                        │
│        📱 User Interface & Experience                      │
│  🌐 Port 4200 | SPA | Responsive Design                  │
└─────────────────┬───────────────────────────────────────────┘
                  │ HTTP/REST API
                  │
┌─────────────────┴───────────────────────────────────────────┐
│                 BACKEND LAYER                               │
│            Spring Boot 3.5 Application                     │
│     🚀 REST API | Business Logic | Data Management        │
│  🌐 Port 8080 | Java 21 | Clean Architecture             │
└─────────────────┬───────────────────────────────────────────┘
                  │ JDBC/JPA
                  │
┌─────────────────┴───────────────────────────────────────────┐
│                DATABASE LAYER                               │
│                PostgreSQL 15+                              │
│        🗄️  Data Persistence & Migrations                   │
│   🌐 Port 5432 | Flyway | ACID Transactions              │
└─────────────────────────────────────────────────────────────┘
```

### Backend Clean Architecture Layers
The Spring Boot API follows Clean/Hexagonal Architecture with clear separation of concerns:

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

### 🛠️ Technology Stack

#### Frontend Stack
- **Angular 20** - Modern SPA framework with signals and standalone components
- **TypeScript 5.9** - Type-safe development
- **Angular CLI** - Development tooling and build system
- **RxJS 7.8** - Reactive programming for async operations
- **Jasmine + Karma** - Unit testing framework
- **Node.js 24.4** - Runtime environment

#### Backend Stack
- **Java 21** - Latest LTS version with modern language features
- **Spring Boot 3.5.5** - Production-ready application framework
- **Spring Data JPA** - Database abstraction and ORM
- **Spring Security** - Authentication and authorization
- **Spring Validation** - Input validation and constraints
- **Maven** - Dependency management and build automation
- **Lombok** - Boilerplate code reduction

#### Database & Infrastructure
- **PostgreSQL 15+** - ACID-compliant relational database
- **Flyway** - Database migration management
- **Docker Compose** - Local development environment
- **JUnit 5** - Unit and integration testing
- **Testcontainers** - Integration testing with real databases

## 📁 Full Stack Project Structure

### Root Directory Overview
```
costify/
├── 📱 apps/web/          # Angular Frontend Application
├── 🚀 apps/api/          # Spring Boot Backend API
├── 🐳 docker-compose.yml # Database infrastructure setup
├── 🔧 Makefile          # Development workflow automation
├── 📋 CLAUDE.md         # Architecture documentation
├── 📖 README.md         # Project overview and setup
└── 🔄 .github/          # CI/CD workflows
```

### Frontend Structure (apps/web/)

```
apps/web/
├── 📱 src/
│   ├── app/
│   │   ├── app.ts                    # Root application component
│   │   ├── app.config.ts            # Application configuration
│   │   ├── app.routes.ts            # Routing configuration
│   │   ├── app.html                 # Root template
│   │   ├── app.css                  # Root styles
│   │   └── app.spec.ts              # Root component tests
│   ├── main.ts                      # Application bootstrap
│   ├── index.html                   # Entry HTML template
│   └── styles.css                   # Global styles
├── 📋 angular.json                  # Angular CLI configuration
├── 📦 package.json                  # Node dependencies and scripts
├── 🔧 tsconfig.json                 # TypeScript configuration
├── 🧪 tsconfig.spec.json           # Test TypeScript configuration
└── 🏠 public/                       # Static assets
    └── favicon.ico
```

### Backend Structure (apps/api/)

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
- **Total Test Files**: 20+ test classes
- **Coverage Areas**: Domain (6 test classes), Application (7 test classes), Integration (7 test classes)
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
├── RegisterIngredientUseCaseTest.java
└── RegisterRecipeUseCaseTest.java
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

```
apps/api/
├── 🚀 src/main/java/br/unifor/costify/
│   ├── CostifyApplication.java                      # Spring Boot main class
│   ├── 🏛️ application/                             # Application Layer (Use Cases & DTOs)
│   │   ├── contracts/                              # Repository interfaces
│   │   ├── dto/                                    # Data Transfer Objects
│   │   │   ├── command/                           # Input DTOs for commands
│   │   │   ├── entity/                            # Output DTOs for entities
│   │   │   └── response/                          # Response DTOs
│   │   ├── errors/                                # Application exceptions
│   │   ├── factory/                               # Entity creation factories
│   │   ├── service/                               # Application services
│   │   ├── validation/                            # Input validation
│   │   └── usecase/                               # Business use cases
│   ├── 💎 domain/                                  # Domain Layer (Core Business Logic)
│   │   ├── contracts/                              # Domain contracts
│   │   ├── entity/                                # Domain entities
│   │   ├── errors/                                # Domain exceptions
│   │   ├── service/                               # Domain services
│   │   └── valueobject/                           # Value objects
│   └── 🔧 infra/                                   # Infrastructure Layer
│       ├── config/                                # Configuration classes
│       ├── controllers/                           # REST controllers
│       └── data/                                  # Database implementation
│           ├── entities/                          # JPA entities
│           └── repositories/                      # Repository implementations
├── 🧪 src/test/java/br/unifor/costify/            # Comprehensive test suite
│   ├── application/                               # Application layer tests
│   ├── domain/                                    # Domain unit tests
│   ├── integration/                               # Integration tests
│   └── TestcontainersConfiguration.java          # Test infrastructure
├── 📋 src/main/resources/
│   ├── application.properties                     # Spring configuration
│   └── db/migration/                              # Flyway database migrations
├── 📦 pom.xml                                     # Maven configuration
├── 🔧 mvnw / mvnw.cmd                            # Maven wrapper scripts
└── 🎯 target/                                     # Build output
```

## 🚀 Full Stack Development Workflow

### 🎯 Quick Start Commands

#### Full Stack Setup
```bash
# Complete project setup (database + dependencies)
make setup

# Check system status
make status

# View all available commands
make help
```

#### Development Servers
```bash
# Start API server (port 8080)
make dev

# Start frontend dev server (port 4200)
make web-dev

# Full stack development guide
make dev-full
```

#### Testing & Building
```bash
# Run all tests (API + frontend)
make test-all

# Build both applications
make build-all

# Install all dependencies
make install-all
```

### 🔧 Individual Component Commands

#### API Commands (Spring Boot)
```bash
# API development
cd apps/api
./mvnw spring-boot:run              # Start API server
./mvnw test -DargLine="-ea"          # Run tests with assertions
./mvnw clean install                 # Full build with tests
./mvnw test -Dtest=IngredientTest   # Run specific test
```

#### Frontend Commands (Angular)
```bash
# Frontend development
cd apps/web
npm start                           # Start dev server
npm run build                       # Production build
npm test                            # Run unit tests
npm install                         # Install dependencies
```

#### Database Commands
```bash
# Database management
make docker-up                     # Start PostgreSQL
make docker-down                   # Stop containers
make docker-reset                  # Reset database with clean state
```

### 💻 Development URLs & Endpoints

#### Frontend (Angular)
- **Development Server**: http://localhost:4200
- **Hot Reload**: Automatic browser refresh on file changes
- **Angular DevTools**: Browser extension for debugging

#### Backend API (Spring Boot)
- **API Base URL**: http://localhost:8080/api
- **Health Check**: http://localhost:8080/actuator/health
- **Ingredients API**: http://localhost:8080/api/ingredients
- **Recipes API**: http://localhost:8080/api/recipes
- **API Documentation**: Available via Swagger/OpenAPI (future enhancement)

#### Database (PostgreSQL)
- **Host**: localhost:5432
- **Database**: costify
- **Username**: postgres
- **Password**: postgres
- **Admin Tool**: pgAdmin or any PostgreSQL client

### 🔄 Recommended Development Workflow

1. **Initial Setup**
   ```bash
   git clone <repository>
   make setup                    # Setup database + install dependencies
   ```

2. **Daily Development**
   ```bash
   make status                   # Check what's running
   make dev                      # Terminal 1: Start API
   make web-dev                  # Terminal 2: Start frontend
   ```

3. **Code Quality**
   ```bash
   make test-all                 # Run all tests
   make build-all               # Build both applications
   ```

4. **Database Operations**
   ```bash
   make docker-reset            # Reset database for clean state
   # Database migrations run automatically on API startup
   ```

## 🏗️ API Architecture Deep Dive

### REST API Endpoints

#### Ingredients API
```http
POST /api/ingredients
Content-Type: application/json

{
  "name": "All-purpose flour",
  "packageQuantity": 1000,
  "packagePrice": 2.50,
  "packageUnit": "GRAMS"
}
```

#### Recipes API
```http
POST /api/recipes
Content-Type: application/json

{
  "name": "Classic Bread",
  "ingredients": [
    {
      "ingredientId": "uuid-here",
      "quantity": 500,
      "unit": "GRAMS"
    }
  ]
}
```

### Database Schema

#### Core Tables
- **ingredients**: Stores ingredient master data with pricing
- **recipes**: Recipe metadata and calculated total costs
- **recipe_ingredients**: Many-to-many relationship with quantities
- **flyway_schema_history**: Database migration tracking

#### Supported Units
- **Weight**: GRAMS, KILOGRAMS, POUNDS, OUNCES
- **Volume**: MILLILITERS, LITERS, CUPS, TABLESPOONS, TEASPOONS
- **Count**: PIECES, UNITS

### 🎨 Frontend Architecture (Angular 20)

#### Modern Angular Features
- **Standalone Components**: No NgModule required
- **Signals**: Reactive state management
- **Control Flow**: New @if, @for, @switch syntax
- **TypeScript 5.9**: Latest language features
- **Angular Material**: UI component library (future enhancement)

#### Component Structure
```typescript
// Example component structure
@Component({
  selector: 'app-recipe-cost',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  template: './recipe-cost.component.html',
  styles: ['./recipe-cost.component.scss']
})
export class RecipeCostComponent {
  private readonly http = inject(HttpClient);
  protected readonly recipe = signal<Recipe | null>(null);
  
  calculateCost(recipeId: string) {
    // Business logic here
  }
}
```

## 🎯 Key Design Principles

### Domain-Driven Design (Backend)
- **Entities**: `Ingredient` and `Recipe` aggregates with business identity and behavior
- **Value Objects**: `Money`, `Unit`, `RecipeIngredient` for immutable domain concepts  
- **Domain Services**: `RecipeCostCalculationService` for complex business rules
- **Repository Interfaces**: `IngredientRepository`, `RecipeRepository` for data persistence abstraction
- **Use Cases**: `RegisterIngredientUseCase`, `CalculateRecipeCostUseCase` for application workflows

### Component-Based Architecture (Frontend)
- **Standalone Components**: Self-contained, reusable UI components
- **Reactive Forms**: Type-safe form handling with validation
- **Services**: Shared business logic and HTTP communication
- **Signals**: Modern reactive state management
- **Routing**: Lazy-loaded feature modules for performance

### Full Stack Benefits
- **Separation of Concerns**: Frontend handles UX, backend handles business logic
- **Independent Scaling**: Frontend and backend can scale independently
- **Technology Flexibility**: Can replace frontend or backend without affecting the other
- **Team Specialization**: Frontend and backend teams can work independently
- **API-First Design**: REST API can serve multiple frontends (web, mobile, etc.)
- **Testability**: Each layer and application can be tested independently
- **Maintainability**: Clear boundaries between presentation, business, and data layers

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

## 📈 Current System Capabilities

### ✅ Implemented Features

#### Backend API
- **Complete Recipe Management**: CRUD operations with REST endpoints
- **Complete Ingredient Management**: Registration and cost calculation
- **Recipe Cost Engine**: Automatic cost calculation with ingredient breakdown
- **Clean Architecture**: Domain-driven design with proper layer separation
- **Database Integration**: PostgreSQL with Flyway migrations
- **Comprehensive Testing**: Unit, integration, and repository tests
- **Security Configuration**: Spring Security foundation
- **Input Validation**: Request validation with error handling

#### Frontend Application
- **Modern Angular 20**: Latest framework with standalone components
- **TypeScript 5.9**: Full type safety and modern language features
- **Development Ready**: Hot reload and development server
- **Testing Framework**: Jasmine and Karma configured
- **Build System**: Angular CLI with production optimization
- **Responsive Foundation**: Mobile-first CSS framework ready

#### Development Infrastructure
- **Full Stack Makefile**: Complete development workflow automation
- **Docker Integration**: PostgreSQL containerization
- **CI/CD Ready**: GitHub Actions workflow configured
- **Documentation**: Comprehensive architecture and setup guides

### 🎯 Business Value Delivered
1. **Professional Recipe Costing**: Calculate precise ingredient costs for recipes
2. **Scalable Architecture**: Ready for enterprise-level expansion
3. **Modern Tech Stack**: Built with latest industry-standard technologies
4. **Developer Experience**: Streamlined workflow with automated tooling
5. **Production Ready**: Security, testing, and deployment foundations in place

## 🚀 Future Enhancement Opportunities

### 📱 Frontend Features
- **Recipe Management UI**: Create, edit, and view recipes with real-time cost calculation
- **Ingredient Library**: Manage ingredient database with pricing updates
- **Cost Analytics Dashboard**: Visual cost breakdowns and recipe comparisons
- **Responsive Design**: Mobile-optimized for kitchen use
- **User Authentication**: Login and user management integration
- **Offline Capability**: PWA features for kitchen environments
- **Print Recipes**: Professional recipe cards with cost information

### 🚀 Backend Enhancements
- **Recipe Scaling**: Calculate costs for different batch sizes
- **Price History**: Track ingredient price changes over time
- **Recipe Versioning**: Version control for recipe modifications
- **Advanced Search**: Full-text search for recipes and ingredients
- **Bulk Operations**: Import/export recipes and ingredients
- **Recipe Categories**: Organize recipes by type, cuisine, or dietary restrictions
- **Nutritional Data**: Integration with nutrition databases
- **Cost Optimization**: Suggest ingredient substitutions for cost savings

### 🔧 Technical Improvements
- **API Documentation**: OpenAPI/Swagger integration with interactive docs
- **Caching Layer**: Redis for performance optimization
- **Event Sourcing**: Track all recipe and ingredient changes
- **Monitoring**: Application performance monitoring and alerting
- **Multi-tenancy**: Support for multiple restaurants/businesses
- **Microservices**: Split into specialized services as system grows
- **Advanced Security**: OAuth2, JWT tokens, role-based access control
- **Audit Logging**: Complete change tracking for compliance

### 📊 Analytics & Reporting
- **Cost Trends**: Analyze ingredient price trends over time
- **Recipe Profitability**: Calculate profit margins with selling prices
- **Usage Analytics**: Track most popular recipes and ingredients
- **Cost Alerts**: Notifications when ingredient prices change significantly
- **Batch Cost Planning**: Optimize production runs for cost efficiency
- **Supplier Management**: Track ingredient sources and supplier pricing

### 🌐 Integration Possibilities
- **ERP Integration**: Connect with restaurant management systems
- **Inventory Management**: Real-time ingredient stock tracking
- **Supplier APIs**: Automatic price updates from food suppliers
- **POS Integration**: Connect with point-of-sale systems
- **Accounting Integration**: Export cost data to accounting software
- **Mobile Apps**: Native iOS/Android applications
- **Third-party APIs**: Nutrition, allergen, and dietary information