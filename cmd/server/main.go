package main

import (
	"log"

	"github.com/gin-gonic/gin"
	"github.com/vini/costify-go/internal/application/factory"
	"github.com/vini/costify-go/internal/application/service"
	"github.com/vini/costify-go/internal/application/usecase"
	domainservice "github.com/vini/costify-go/internal/domain/service"
	"github.com/vini/costify-go/internal/infrastructure/config"
	"github.com/vini/costify-go/internal/infrastructure/database"
	"github.com/vini/costify-go/internal/infrastructure/database/repository"
	"github.com/vini/costify-go/internal/infrastructure/http"
	"github.com/vini/costify-go/internal/infrastructure/http/handler"
)

func main() {
	// Load configuration
	cfg := config.LoadConfig()

	// Setup database connection
	db, err := database.NewGormConnection(&cfg.Database)
	if err != nil {
		log.Fatal("Failed to connect to database:", err)
	}

	// Run migrations
	sqlDB, err := database.NewSQLConnection(&cfg.Database)
	if err != nil {
		log.Fatal("Failed to create SQL connection for migrations:", err)
	}
	defer sqlDB.Close()

	if err := database.RunMigrations(sqlDB, "migrations"); err != nil {
		log.Fatal("Failed to run migrations:", err)
	}

	// Setup dependencies
	idGenerator := config.NewUuidGenerator()

	// Repositories
	ingredientRepo := repository.NewIngredientRepository(db)
	recipeRepo := repository.NewRecipeRepository(db)

	// Factories
	ingredientFactory := factory.NewIngredientFactory(idGenerator)
	recipeFactory := factory.NewRecipeFactory(idGenerator)

	// Services
	ingredientLoaderService := service.NewIngredientLoaderService(ingredientRepo)
	costCalculationService := domainservice.NewRecipeCostCalculationService()

	// Use cases
	registerIngredientUseCase := usecase.NewRegisterIngredientUseCase(ingredientRepo, ingredientFactory)
	registerRecipeUseCase := usecase.NewRegisterRecipeUseCase(recipeRepo, ingredientLoaderService, recipeFactory, costCalculationService)
	calculateRecipeCostUseCase := usecase.NewCalculateRecipeCostUseCase(recipeRepo, ingredientLoaderService, costCalculationService)

	// Handlers
	ingredientHandler := handler.NewIngredientHandler(registerIngredientUseCase)
	recipeHandler := handler.NewRecipeHandler(registerRecipeUseCase, calculateRecipeCostUseCase)

	// Setup HTTP server
	gin.SetMode(cfg.Server.Mode)
	engine := gin.Default()

	// Setup routes
	router := http.NewRouter(ingredientHandler, recipeHandler)
	router.SetupRoutes(engine)

	// Start server
	log.Printf("Starting server on port %s", cfg.Server.Port)
	if err := engine.Run(":" + cfg.Server.Port); err != nil {
		log.Fatal("Failed to start server:", err)
	}
}
