package http

import (
	"github.com/gin-gonic/gin"
	"github.com/vini/costify-go/internal/infrastructure/http/handler"
)

// Router sets up HTTP routes
type Router struct {
	ingredientHandler *handler.IngredientHandler
	recipeHandler     *handler.RecipeHandler
}

// NewRouter creates a new Router
func NewRouter(
	ingredientHandler *handler.IngredientHandler,
	recipeHandler *handler.RecipeHandler,
) *Router {
	return &Router{
		ingredientHandler: ingredientHandler,
		recipeHandler:     recipeHandler,
	}
}

// SetupRoutes configures all HTTP routes
func (r *Router) SetupRoutes(engine *gin.Engine) {
	// API version 1
	v1 := engine.Group("/api/v1")
	{
		// Ingredient routes
		ingredients := v1.Group("/ingredients")
		{
			ingredients.POST("", r.ingredientHandler.RegisterIngredient)
		}

		// Recipe routes
		recipes := v1.Group("/recipes")
		{
			recipes.POST("", r.recipeHandler.RegisterRecipe)
			recipes.GET("/:id/cost", r.recipeHandler.CalculateRecipeCost)
		}
	}

	// Health check
	engine.GET("/health", func(c *gin.Context) {
		c.JSON(200, gin.H{"status": "ok"})
	})
}
