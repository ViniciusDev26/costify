# Costify API Makefile
# Development commands for Costify Spring Boot API

.PHONY: help run dev test build clean install docker-up docker-down lint check

# Default target
help: ## Show this help message
	@echo "Costify API Development Commands:"
	@echo ""
	@grep -E '^[a-zA-Z_-]+:.*?## .*$$' $(MAKEFILE_LIST) | sort | awk 'BEGIN {FS = ":.*?## "}; {printf "  \033[36m%-15s\033[0m %s\n", $$1, $$2}'
	@echo ""

# API Commands
run: docker-up ## Start the API server
	cd apps/api && ./mvnw spring-boot:run

dev: docker-up ## Start API in development mode with hot reload
	cd apps/api && ./mvnw spring-boot:run -Dspring.profiles.active=dev

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
	@echo "🔍 Checking Costify API status..."
	@echo ""
	@echo "📊 Database (PostgreSQL):"
	@docker-compose ps postgres || echo "❌ PostgreSQL not running. Run 'make docker-up' first."
	@echo ""
	@echo "🚀 API Status:"
	@curl -s http://localhost:8080/actuator/health 2>/dev/null | grep -q "UP" && echo "✅ API is running at http://localhost:8080" || echo "❌ API not running. Run 'make run' first."

# Quick Development Workflow
setup: docker-up install ## Initial project setup (database + dependencies)
	@echo "✅ Costify API setup complete!"
	@echo "📖 Run 'make run' to start the API server"

restart: docker-down clean setup run ## Full restart (clean database + rebuild + run)

# Development Info
info: ## Show development information
	@echo "🏗️  Costify API Development Environment"
	@echo ""
	@echo "📁 Project Structure:"
	@echo "   • API: apps/api/"
	@echo "   • Source: apps/api/src/main/java/"
	@echo "   • Tests: apps/api/src/test/java/"
	@echo ""
	@echo "🌐 Endpoints:"
	@echo "   • API Base: http://localhost:8080"
	@echo "   • Health Check: http://localhost:8080/actuator/health"
	@echo "   • Ingredients: http://localhost:8080/ingredients"
	@echo "   • Recipes: http://localhost:8080/recipes"
	@echo ""
	@echo "🗄️  Database:"
	@echo "   • PostgreSQL: localhost:5432"
	@echo "   • Database: costify"
	@echo "   • User: costify"
	@echo ""
	@echo "🔧 Useful Commands:"
	@echo "   • make run      - Start API server"
	@echo "   • make test     - Run all tests"
	@echo "   • make setup    - Initial setup"
	@echo "   • make status   - Check system status"