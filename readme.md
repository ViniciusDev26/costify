# Costify TypeScript

A modern TypeScript platform for calculating product costs from recipes using Clean Architecture principles. Built with **Bun + Elysia** for high performance and **Decimal.js** for precise financial calculations.

## 🚀 Features

- **Ingredient Management**: Create, update, and manage ingredients with precise pricing
- **Recipe Management**: Build recipes with multiple ingredients and automatic cost calculation
- **Cost Calculation**: Real-time recipe cost calculation with ingredient breakdown
- **Clean Architecture**: Domain-driven design with clear separation of concerns
- **Precise Math**: Uses Decimal.js for accurate financial calculations (no floating-point errors)
- **Modern Stack**: Bun runtime + Elysia framework + PostgreSQL + Prisma ORM

## 🏗️ Architecture

```
┌─────────────────────────────────────────────────────────────┐
│                    Presentation Layer                       │
│            (Elysia Controllers, HTTP Routes)               │
└─────────────────┬───────────────────────────────────────────┘
                  │
┌─────────────────┴───────────────────────────────────────────┐
│                  Application Layer                          │
│        (Use Cases, DTOs, Application Services)             │
└─────────────────┬───────────────────────────────────────────┘
                  │
┌─────────────────┴───────────────────────────────────────────┐
│                    Domain Layer                             │
│    (Entities, Value Objects, Domain Services)              │
└─────────────────┬───────────────────────────────────────────┘
                  │
┌─────────────────┴───────────────────────────────────────────┐
│                Infrastructure Layer                         │
│        (Prisma Repositories, External APIs)                │
└─────────────────────────────────────────────────────────────┘
```

## 🛠️ Tech Stack

- **Runtime**: [Bun](https://bun.sh) (Fast JavaScript runtime)
- **Framework**: [Elysia](https://elysiajs.com) (Fast and lightweight HTTP framework)
- **Database**: PostgreSQL 16 + Prisma ORM
- **Language**: TypeScript
- **Testing**: Vitest + Testcontainers
- **Linting/Formatting**: Biome
- **Math**: Decimal.js (for precise financial calculations)

## 📦 Installation

### Prerequisites

- [Bun](https://bun.sh) >= 1.0.0
- Docker & Docker Compose (for database)

### Setup

1. **Clone the repository**
   ```bash
   git clone <repository-url>
   cd costify
   ```

2. **Install dependencies**
   ```bash
   bun install
   ```

3. **Start the database**
   ```bash
   docker compose up -d
   ```

4. **Setup database**
   ```bash
   # Generate Prisma client
   bun db:generate
   
   # Run migrations
   bun db:migrate
   ```

5. **Start development server**
   ```bash
   bun dev
   ```

The API will be available at `http://localhost:3000`

## 🔧 Development Commands

```bash
# Development
bun dev                    # Start development server with hot reload
bun start                  # Start production server
bun build                  # Build for production

# Database
bun db:generate           # Generate Prisma client
bun db:migrate            # Run database migrations
bun db:deploy             # Deploy migrations (production)
bun db:studio             # Open Prisma Studio

# Testing
bun test                  # Run tests
bun test:coverage         # Run tests with coverage
bun test:ui               # Run tests with UI

# Code Quality
bun lint                  # Run linter
bun lint:fix              # Fix linting issues
bun format                # Format code
bun check                 # Run all checks (lint + format)
```

## 📡 API Endpoints

### Ingredients

- `POST /api/v1/ingredients` - Create ingredient
- `GET /api/v1/ingredients` - List all ingredients
- `GET /api/v1/ingredients/:id` - Get ingredient by ID
- `PUT /api/v1/ingredients/:id` - Update ingredient
- `DELETE /api/v1/ingredients/:id` - Delete ingredient

### Recipes

- `POST /api/v1/recipes` - Create recipe
- `GET /api/v1/recipes` - List all recipes
- `GET /api/v1/recipes/:id` - Get recipe by ID
- `GET /api/v1/recipes/:id/cost` - Calculate recipe cost
- `DELETE /api/v1/recipes/:id` - Delete recipe

### Health Check

- `GET /health` - Application health status

## 💰 Decimal Precision

This application uses **Decimal.js** to ensure precise financial calculations:

```typescript
// ❌ JavaScript floating point problems
0.1 + 0.2 // = 0.30000000000000004

// ✅ Costify with Decimal.js
new Money('0.1').add(new Money('0.2')) // = 0.30 (exact!)
```

All monetary values are handled through the `Money` value object, which uses Decimal.js internally via dependency inversion.

## 🧪 Testing

The project includes comprehensive testing:

- **Unit Tests**: Domain entities, value objects, and use cases
- **Integration Tests**: Database repositories with Testcontainers
- **API Tests**: HTTP endpoints (planned)

```bash
# Run all tests
bun test

# Run with coverage
bun test:coverage

# Run integration tests only
bun test integration

# Run specific test file
bun test src/domain/entities/Money.test.ts
```

## 🏛️ Clean Architecture Principles

### Domain Layer (`src/domain/`)
- **Entities**: Core business objects (`Ingredient`, `Recipe`)
- **Value Objects**: Immutable objects (`Money`, `Unit`, `Id`)
- **Services**: Domain logic (`RecipeCostCalculationService`)
- **Contracts**: Interfaces for external dependencies

### Application Layer (`src/application/`)
- **Use Cases**: Business workflows (`RegisterIngredientUseCase`)
- **DTOs**: Data transfer objects for communication
- **Mappers**: Convert between domain and DTOs
- **Factories**: Create domain objects from commands

### Infrastructure Layer (`src/infrastructure/`)
- **Controllers**: HTTP request handlers (Elysia)
- **Repositories**: Database access (Prisma)
- **Providers**: External service implementations

## 🌍 Environment Variables

```env
# Database
DATABASE_URL="postgresql://costify:costify123@localhost:5432/costify_ts?schema=public"

# Application
NODE_ENV=development
PORT=3000
```

## 📊 Example Usage

### Create an Ingredient
```bash
curl -X POST http://localhost:3000/api/v1/ingredients \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Flour",
    "pricePerUnit": "2.50",
    "unit": "KILOGRAM"
  }'
```

### Create a Recipe
```bash
curl -X POST http://localhost:3000/api/v1/recipes \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Bread",
    "ingredients": [
      {
        "ingredientId": "ingredient-uuid-here",
        "quantity": "0.5",
        "unit": "KILOGRAM"
      }
    ]
  }'
```

### Calculate Recipe Cost
```bash
curl http://localhost:3000/api/v1/recipes/{recipe-id}/cost
```

## 🚀 Production Deployment

1. **Build the application**
   ```bash
   bun build
   ```

2. **Setup production database**
   ```bash
   bun db:deploy
   ```

3. **Start production server**
   ```bash
   bun start
   ```

## 🤝 Contributing

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/amazing-feature`)
3. Make your changes following the architecture principles
4. Add tests for your changes
5. Run the test suite (`bun test`)
6. Check code quality (`bun check`)
7. Commit your changes (`git commit -m 'Add amazing feature'`)
8. Push to the branch (`git push origin feature/amazing-feature`)
9. Open a Pull Request

## 📝 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## 🔧 Troubleshooting

### Database Connection Issues
```bash
# Check if PostgreSQL is running
docker compose ps

# Restart database
docker compose restart postgres

# Check logs
docker compose logs postgres
```

### TypeScript Compilation Issues
```bash
# Clear and reinstall dependencies
rm -rf node_modules bun.lockb
bun install

# Regenerate Prisma client
bun db:generate
```

## 📚 Architecture Documentation

For detailed architecture documentation, see [CLAUDE.md](CLAUDE.md).