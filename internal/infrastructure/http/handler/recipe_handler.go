package handler

import (
	"net/http"

	"github.com/gin-gonic/gin"
	"github.com/vini/costify-go/internal/application/dto/command"
	"github.com/vini/costify-go/internal/application/usecase"
	"github.com/vini/costify-go/internal/infrastructure/http/dto"
)

// RecipeHandler handles HTTP requests for recipes
type RecipeHandler struct {
	registerRecipeUseCase      *usecase.RegisterRecipeUseCase
	calculateRecipeCostUseCase *usecase.CalculateRecipeCostUseCase
}

// NewRecipeHandler creates a new RecipeHandler
func NewRecipeHandler(
	registerRecipeUseCase *usecase.RegisterRecipeUseCase,
	calculateRecipeCostUseCase *usecase.CalculateRecipeCostUseCase,
) *RecipeHandler {
	return &RecipeHandler{
		registerRecipeUseCase:      registerRecipeUseCase,
		calculateRecipeCostUseCase: calculateRecipeCostUseCase,
	}
}

// RegisterRecipe handles recipe registration
func (h *RecipeHandler) RegisterRecipe(c *gin.Context) {
	var request dto.RecipeRegisterRequest
	
	if err := c.ShouldBindJSON(&request); err != nil {
		c.JSON(http.StatusBadRequest, gin.H{"error": err.Error()})
		return
	}

	// Convert ingredients to domain value objects
	ingredients, err := request.ToRecipeIngredients()
	if err != nil {
		c.JSON(http.StatusBadRequest, gin.H{"error": err.Error()})
		return
	}

	// Create command
	cmd, err := command.NewRegisterRecipeCommand(request.Name, ingredients)
	if err != nil {
		c.JSON(http.StatusBadRequest, gin.H{"error": err.Error()})
		return
	}

	// Execute use case
	result, err := h.registerRecipeUseCase.Execute(cmd)
	if err != nil {
		c.JSON(http.StatusInternalServerError, gin.H{"error": err.Error()})
		return
	}

	c.JSON(http.StatusCreated, result)
}

// CalculateRecipeCost handles recipe cost calculation
func (h *RecipeHandler) CalculateRecipeCost(c *gin.Context) {
	recipeId := c.Param("id")
	if recipeId == "" {
		c.JSON(http.StatusBadRequest, gin.H{"error": "Recipe ID is required"})
		return
	}

	// Execute use case
	result, err := h.calculateRecipeCostUseCase.Execute(recipeId)
	if err != nil {
		c.JSON(http.StatusInternalServerError, gin.H{"error": err.Error()})
		return
	}

	c.JSON(http.StatusOK, result)
}