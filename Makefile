# Costify Full Stack Makefile
# Development commands for Costify API (Spring Boot) and Frontend (Angular)

.PHONY: help run dev test build clean install docker-up docker-down lint check web-dev web-build web-test web-install

# Default target
help: ## Show this help message
	@echo "Costify Full Stack Development Commands:"
	@echo ""
	@grep -E '^[a-zA-Z_-]+:.*?## .*$$' $(MAKEFILE_LIST) | sort | awk 'BEGIN {FS = ":.*?## "}; {printf "  \033[36m%-15s\033[0m %s\n", $$1, $$2}'
	@echo ""

# API Commands
run: docker-up ## Start the API server
	cd apps/api && ./mvnw spring-boot:run

dev: docker-up ## Start API in development mode with hot reload
	cd apps/api && ./mvnw spring-boot:run -Dspring.profiles.active=dev

# Frontend Commands
web-dev: ## Start the Angular frontend in development mode
	cd apps/web && npm start

web-build: ## Build the Angular frontend for production
	cd apps/web && npm run build

web-test: ## Run Angular frontend tests
	cd apps/web && npm test

web-install: ## Install frontend dependencies
	cd apps/web && npm install

# Build Commands
build: ## Build the application
	cd apps/api && ./mvnw clean compile

test: ## Run all tests
	cd apps/api && ./mvnw test -DargLine="-ea"

test-integration: ## Run integration tests only
	cd apps/api && ./mvnw test -Dtest="**/*IntegrationTest"

install: ## Clean build and install dependencies
	cd apps/api && ./mvnw clean install

# Code Quality
check: ## Run code checks and validation
	cd apps/api && ./mvnw verify

lint: ## Check code style (placeholder - add when linting is configured)
	@echo "Code linting not configured yet"

# Database Commands
docker-up: ## Start PostgreSQL database
	docker-compose up -d postgres

docker-down: ## Stop all Docker containers
	docker-compose down

docker-reset: docker-down ## Reset database (stop containers and remove volumes)
	docker-compose down -v
	docker-compose up -d postgres

# Utility Commands
clean: ## Clean build artifacts
	cd apps/api && ./mvnw clean

logs: ## Show API application logs (if running in background)
	cd apps/api && tail -f target/logs/application.log 2>/dev/null || echo "No log file found. Run 'make run' first."

status: ## Check application and database status
	@echo "🔍 Checking Costify Full Stack status..."
	@echo ""
	@echo "📊 Database (PostgreSQL):"
	@docker-compose ps postgres || echo "❌ PostgreSQL not running. Run 'make docker-up' first."
	@echo ""
	@echo "🚀 API Status:"
	@curl -s http://localhost:8080/actuator/health 2>/dev/null | grep -q "UP" && echo "✅ API is running at http://localhost:8080" || echo "❌ API not running. Run 'make dev' first."
	@echo ""
	@echo "🌐 Frontend Status:"
	@curl -s http://localhost:4200 2>/dev/null >/dev/null && echo "✅ Frontend is running at http://localhost:4200" || echo "❌ Frontend not running. Run 'make web-dev' first."

# Full Stack Commands
dev-full: ## Start both API and frontend in development mode (requires 2 terminals)
	@echo "🚀 Starting full stack development..."
	@echo "📊 This will start both API (port 8080) and frontend (port 4200)"
	@echo "⚠️  You need to run this in two separate terminals:"
	@echo "   Terminal 1: make dev"
	@echo "   Terminal 2: make web-dev"
	@echo "   Or use: make dev & make web-dev"

build-all: build web-build ## Build both API and frontend

test-all: test web-test ## Run all tests (API + frontend)

install-all: install web-install ## Install all dependencies (API + frontend)

# Quick Development Workflow
setup: docker-up install web-install ## Initial project setup (database + dependencies + frontend)
	@echo "✅ Costify full stack setup complete!"
	@echo "📖 Run 'make dev' for API and 'make web-dev' for frontend"

restart: docker-down clean setup run ## Full restart (clean database + rebuild + run)

# Development Info
info: ## Show development information
	@echo "🏗️  Costify Full Stack Development Environment"
	@echo ""
	@echo "📁 Project Structure:"
	@echo "   • API: apps/api/ (Spring Boot + Java 21)"
	@echo "   • Frontend: apps/web/ (Angular 20)"
	@echo "   • Source: apps/api/src/main/java/"
	@echo "   • Tests: apps/api/src/test/java/"
	@echo ""
	@echo "🌐 Development URLs:"
	@echo "   • Frontend: http://localhost:4200"
	@echo "   • API Base: http://localhost:8080/api"
	@echo "   • Health Check: http://localhost:8080/actuator/health"
	@echo "   • Ingredients: http://localhost:8080/api/ingredients"
	@echo "   • Recipes: http://localhost:8080/api/recipes"
	@echo ""
	@echo "🗄️  Database:"
	@echo "   • PostgreSQL: localhost:5432"
	@echo "   • Database: costify"
	@echo "   • User: postgres"
	@echo ""
	@echo "🔧 Essential Commands:"
	@echo "   • make setup      - Initial full stack setup"
	@echo "   • make dev        - Start API server"
	@echo "   • make web-dev    - Start frontend dev server"
	@echo "   • make dev-full   - Instructions for full stack dev"
	@echo "   • make test-all   - Run all tests (API + frontend)"
	@echo "   • make status     - Check system status"