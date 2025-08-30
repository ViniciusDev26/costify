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

---

## Error Handling Rules

### 🚨 Clean Architecture Error Handling

Costify implements **layer-based error handling** following Clean Architecture principles. Each layer has specific error responsibilities and codes:

#### **Layer Error Code Ranges**
- **Domain Layer**: `DOM-1000` to `DOM-1999` - Core business rule violations
- **Application Layer**: `APP-2000` to `APP-2999` - Use case and workflow errors  
- **Infrastructure Layer**: `INF-3000` to `INF-3999` - Technical and external system errors

### 📋 Error Response Format

All API errors follow a consistent JSON structure:

```json
{
  "error": {
    "code": "DOM-1001",
    "message": "Ingredient name cannot be null or blank",
    "details": "The ingredient name 'null' is invalid",
    "timestamp": "2025-01-15T10:30:00Z",
    "path": "/ingredients"
  }
}
```

### 🎯 Domain Layer Errors (DOM-1000 to DOM-1999)

#### **Ingredient Domain Errors**
- **`DOM-1001`** - `InvalidIngredientNameException`: Ingredient name is null, blank, or invalid
- **`DOM-1002`** - `NegativeIngredientQuantityException`: Package quantity must be positive
- **`DOM-1003`** - `NegativeIngredientPriceException`: Package price must be positive

#### **Recipe Domain Errors**  
- **`DOM-1101`** - `EmptyRecipeException`: Recipe must contain at least one ingredient
- **`DOM-1102`** - `InvalidQuantityException`: Recipe ingredient quantity must be positive
- **`DOM-1103`** - `InvalidTotalCostException`: Recipe total cost calculation failed

#### **Money Value Object Errors**
- **`DOM-1201`** - `NegativeMoneyException`: Money amounts cannot be negative
- **`DOM-1202`** - `InvalidCurrencyException`: Invalid or unsupported currency

### ⚙️ Application Layer Errors (APP-2000 to APP-2999)

#### **Use Case Errors**
- **`APP-2001`** - `IngredientNotFoundException`: Ingredient with specified ID does not exist
- **`APP-2002`** - `IngredientAlreadyExistsException`: Ingredient with name already exists
- **`APP-2003`** - `RecipeNotFoundException`: Recipe with specified ID does not exist
- **`APP-2004`** - `RecipeAlreadyExistsException`: Recipe with name already exists

#### **Validation Errors**
- **`APP-2101`** - `InvalidCommandException`: Input command validation failed
- **`APP-2102`** - `ValidationException`: DTO field validation errors
- **`APP-2103`** - `BusinessRuleViolationException`: Application-level business rule violation

### 🔧 Infrastructure Layer Errors (INF-3000 to INF-3999)

#### **Database Errors**
- **`INF-3001`** - `DatabaseConnectionException`: Database connection failed
- **`INF-3002`** - `DatabaseConstraintViolationException`: Database constraint violation
- **`INF-3003`** - `OptimisticLockException`: Concurrent modification detected

#### **External System Errors**
- **`INF-3101`** - `ExternalApiException`: External service call failed
- **`INF-3102`** - `NetworkTimeoutException`: Network request timed out
- **`INF-3103`** - `AuthenticationException`: Authentication with external system failed

### 🛡️ Error Handling Best Practices

#### **1. Error Propagation Rules**
- **Domain → Application**: Domain exceptions bubble up unchanged
- **Application → Infrastructure**: Application exceptions converted to appropriate HTTP status codes  
- **Infrastructure → Client**: Technical errors masked, business errors exposed

#### **2. HTTP Status Code Mapping**
```
Domain Errors (DOM-*)     → 400 Bad Request (business rule violations)
Application Not Found     → 404 Not Found (entity not found)
Application Conflicts     → 409 Conflict (already exists)
Infrastructure Database   → 500 Internal Server Error (technical issues)
Infrastructure External   → 502 Bad Gateway (external service issues)
Validation Errors        → 422 Unprocessable Entity (input validation)
```

#### **3. Security Considerations**
- **Domain errors**: Safe to expose (business rules)
- **Application errors**: Safe to expose (workflow issues)
- **Infrastructure errors**: **Never expose internal details** (database schema, internal IDs, stack traces)

### 🧪 Testing Error Scenarios

#### **Unit Test Examples**
```java
@Test
void shouldThrowInvalidIngredientNameException() {
    // Domain layer validation
    assertThrows(InvalidIngredientNameException.class, 
        () -> new Ingredient(null, packageQuantity, packagePrice, unit));
}

@Test 
void shouldThrowIngredientNotFoundExceptionWhenIngredientDoesNotExist() {
    // Application layer error handling
    when(repository.findById(id)).thenReturn(Optional.empty());
    assertThrows(IngredientNotFoundException.class,
        () -> useCase.execute(command));
}
```

#### **Integration Test Examples**
```java
@Test
void shouldReturn404WhenIngredientNotFound() {
    // Infrastructure layer HTTP error mapping
    mockMvc.perform(get("/ingredients/{id}", nonExistentId))
           .andExpect(status().isNotFound())
           .andExpect(jsonPath("$.error.code").value("APP-2001"));
}
```

### 📊 Error Monitoring & Logging

#### **Logging Levels by Layer**
- **Domain errors**: `WARN` level (business rule violations)
- **Application errors**: `ERROR` level (use case failures)  
- **Infrastructure errors**: `ERROR` level (technical failures)

#### **Error Metrics Tracking**
- Error rate by layer and error code
- Most frequent business rule violations
- Infrastructure error patterns and trends
- Response time impact of error handling

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
