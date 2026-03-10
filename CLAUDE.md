# Costify - Architecture Overview

## Project Summary
Costify is a Java + React monorepo for calculating product costs from recipes. Each ingredient has quantity, unit, and price data to compute real recipe costs.

The project is organized as a monorepo with two modules:
- **`api/`** — Java 21 + Spring Boot backend with Clean Architecture
- **`web/`** — React 19 + TypeScript frontend

## Monorepo Structure

```
costify/
├── api/                    # Backend module (Java/Spring Boot)
├── web/                    # Frontend module (React/TypeScript)
├── docs/                   # Database ER diagram and documentation
├── Makefile                # Unified build orchestration
├── docker-compose.yml      # Full stack (postgres + api + web)
└── .github/workflows/      # CI/CD pipelines
```

## Technology Stack

### Backend (`api/`)
- Java 21, Spring Boot 3.5.5, Maven
- PostgreSQL 16.9, Flyway (migrations)
- JUnit 5, Testcontainers
- Lombok, Spring Security

### Frontend (`web/`)
- React 19, TypeScript 5.8
- Vite (rolldown-vite), TailwindCSS v4, shadcn/ui
- Zustand (state), React Router v7, React Query
- Bun (package manager), Vitest (tests), Biome (linter)

## Backend Architecture (Clean Architecture)

```
┌─────────────────────────────────────────────────────────────┐
│                    Presentation Layer                       │
│                   (Controllers, DTOs)                       │
└─────────────────┬───────────────────────────────────────────┘
                  │
┌─────────────────┴───────────────────────────────────────────┐
│                  Application Layer                          │
│              (Use Cases, Services)                          │
└─────────────────┬───────────────────────────────────────────┘
                  │
┌─────────────────┴───────────────────────────────────────────┐
│                    Domain Layer                             │
│        (Entities, Value Objects, Contracts)                │
└─────────────────┬───────────────────────────────────────────┘
                  │
┌─────────────────┴───────────────────────────────────────────┐
│                Infrastructure Layer                         │
│       (Repositories, External APIs, Database)              │
└─────────────────────────────────────────────────────────────┘
```

## Current Folder Structure

```
costify/
├── api/
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/br/unifor/costify/
│   │   │   │   ├── CostifyApplication.java
│   │   │   │   ├── application/                     # Application Layer
│   │   │   │   │   ├── contracts/
│   │   │   │   │   │   ├── IngredientRepository.java
│   │   │   │   │   │   └── RecipeRepository.java
│   │   │   │   │   ├── dto/
│   │   │   │   │   │   ├── command/
│   │   │   │   │   │   ├── entity/
│   │   │   │   │   │   └── response/
│   │   │   │   │   ├── errors/
│   │   │   │   │   ├── factory/
│   │   │   │   │   ├── service/
│   │   │   │   │   ├── validation/
│   │   │   │   │   └── usecase/
│   │   │   │   ├── domain/                          # Domain Layer
│   │   │   │   │   ├── contracts/
│   │   │   │   │   ├── entity/
│   │   │   │   │   ├── errors/
│   │   │   │   │   ├── service/
│   │   │   │   │   └── valueobject/
│   │   │   │   └── infra/                           # Infrastructure Layer
│   │   │   │       ├── config/
│   │   │   │       ├── controllers/
│   │   │   │       ├── data/
│   │   │   │       │   ├── entities/
│   │   │   │       │   └── repositories/
│   │   │   │       ├── errors/
│   │   │   │       └── transaction/
│   │   │   └── resources/
│   │   │       ├── application.properties
│   │   │       └── db/migration/
│   │   │           ├── V1__Create_ingredients_and_recipes_tables.sql
│   │   │           ├── V2__Convert_unit_fields_to_enum.sql
│   │   │           ├── V3__Remove_unit_cost_column.sql
│   │   │           └── V4__Add_total_cost_column_to_recipes.sql
│   │   └── test/java/br/unifor/costify/
│   │       ├── domain/
│   │       ├── application/
│   │       ├── infra/
│   │       ├── integration/
│   │       │   ├── flyway/
│   │       │   ├── repository/
│   │       │   ├── security/
│   │       │   ├── controllers/
│   │       │   └── events/
│   │       └── e2e/
│   ├── Dockerfile
│   ├── docker-compose.yml
│   ├── docker-compose.prod.yml
│   ├── Makefile
│   └── pom.xml
├── web/
│   ├── src/
│   │   ├── api/costify/
│   │   │   ├── client.ts
│   │   │   └── queries/
│   │   ├── pages/
│   │   │   ├── home/
│   │   │   ├── ingredients/
│   │   │   └── recipes/
│   │   ├── components/ui/
│   │   ├── stores/
│   │   │   └── theme/
│   │   ├── routes/
│   │   ├── hooks/
│   │   └── lib/
│   ├── Dockerfile
│   ├── package.json
│   ├── bun.lock
│   ├── vite.config.ts
│   ├── vitest.config.ts
│   └── biome.json
├── docs/
│   ├── database-er-diagram.mmd
│   └── README.md
├── .github/workflows/
│   └── build-and-test.yml
├── Makefile
└── docker-compose.yml
```

## Build & Development Commands

**IMPORTANT: This project runs exclusively via Docker.**

### Root Makefile (preferred)

```bash
# Full stack
make up                          # Start all services (postgres + api + web)
make down                        # Stop all services
make deploy                      # Rebuild and start all services
make logs                        # View all logs

# API
make up-db                       # Start only PostgreSQL
make deploy-api                  # Rebuild and start API
make logs-api                    # API logs
make test-api                    # Run all API tests
make test-api-class CLASS=Foo    # Run specific test class

# Web
make dev-web                     # Start web in watch mode (hot reload)
make deploy-web                  # Rebuild and start web
make logs-web                    # Web logs
make test-web                    # Run all web tests
make build-web                   # Production build

# All tests
make test                        # Run API + Web tests
```

### Services & Ports

| Service    | URL                          |
|------------|------------------------------|
| API        | http://localhost:8080/api    |
| Web        | http://localhost:5173        |
| PostgreSQL  | localhost:5432               |

## REST API Endpoints

### Ingredient Endpoints

```http
GET    /api/ingredients
GET    /api/ingredients/{id}
POST   /api/ingredients
PUT    /api/ingredients/{id}
```

### Recipe Endpoints

```http
GET    /api/recipes
GET    /api/recipes/{id}
POST   /api/recipes
PUT    /api/recipes/{id}
GET    /api/recipes/{id}/cost
```

### Unit Endpoints

```http
GET    /api/units
```

**Available Units**: `ML`, `L`, `TBSP` (volume) | `G`, `KG`, `TBSP_BUTTER` (weight) | `UN` (unit)

## CI/CD Pipeline

GitHub Actions (`.github/workflows/build-and-test.yml`) runs on every push/PR to `main`:

| Job                | Command                      | Trigger          |
|--------------------|------------------------------|------------------|
| API Tests          | `make test-api`              | Always           |
| Web Tests          | `make test-web`              | Always           |
| Web Build          | `make build-web`             | Always           |
| Generate ER Diagram| Flyway + er-diagram action   | Push to main only|

## Claude Development Protocol

### Mandatory Pre-Execution Rules
**BEFORE ANY CODE IMPLEMENTATION**, Claude must:

1. **Create TodoWrite Plan**
   - Break down task into specific, testable steps
   - Include TDD cycle for each component
   - Mark one task as `in_progress` at a time

2. **Git Safety Checkpoint**
   ```bash
   git add . && git commit -m "checkpoint: before [task-name]" --allow-empty
   ```
   - Always commit current state before starting
   - **IMPORTANT**: This checkpoint will be squashed with final commit

3. **TDD Development Cycle**
   - **RED**: Write failing test first
   - **GREEN**: Write minimal code to pass
   - **REFACTOR**: Improve code quality

### Standard Implementation Flow

#### Phase 1: Planning & Safety
```bash
git add . && git commit -m "checkpoint: before implementing [feature]"
make up
make test-api   # verify API baseline
make test-web   # verify Web baseline
```

#### Phase 2: TDD Implementation
For backend: write test → run `make test-api-class CLASS=ComponentTest` → implement → pass → refactor

For frontend: write test → run `make test-web` → implement → pass → refactor

#### Phase 3: Integration & Validation
```bash
make deploy     # Rebuild all services
make test       # Full test suite
curl http://localhost:8080/api/actuator/health

# Final squash commit
git reset --soft HEAD~1
git add . && git commit -m "implement [feature] with TDD

- Description of changes
- Key features

Co-Authored-By: Claude <noreply@anthropic.com>"
```

### Rollback Strategy
```bash
git log --oneline -5
git reset --hard [checkpoint-commit-hash]
```

## Key Design Principles

### Backend (Clean Architecture / DDD)
- **Domain Layer**: Pure business logic, framework-agnostic
- **Application Layer**: Use cases, orchestration, application contracts
- **Infrastructure Layer**: JPA repos, REST controllers, Spring config
- **Entities**: `Ingredient`, `Recipe` aggregates
- **Value Objects**: `Id`, `Unit`, `RecipeIngredient`, `Money`

### Frontend (Feature-based Architecture)
- **`pages/`**: Feature folders with components, schemas (Zod), and page components
- **`api/costify/queries/`**: React Query hooks per feature
- **`stores/`**: Zustand stores organized by domain
- **`components/ui/`**: Shared shadcn/ui primitives