package handler

import (
	"net/http"

	"github.com/gin-gonic/gin"
	"github.com/vini/costify-go/internal/application/dto/command"
	"github.com/vini/costify-go/internal/application/usecase"
	"github.com/vini/costify-go/internal/infrastructure/http/dto"
)

// IngredientHandler handles HTTP requests for ingredients
type IngredientHandler struct {
	registerIngredientUseCase *usecase.RegisterIngredientUseCase
}

// NewIngredientHandler creates a new IngredientHandler
func NewIngredientHandler(registerIngredientUseCase *usecase.RegisterIngredientUseCase) *IngredientHandler {
	return &IngredientHandler{
		registerIngredientUseCase: registerIngredientUseCase,
	}
}

// RegisterIngredient handles ingredient registration
func (h *IngredientHandler) RegisterIngredient(c *gin.Context) {
	var request dto.IngredientRegisterRequest
	
	if err := c.ShouldBindJSON(&request); err != nil {
		c.JSON(http.StatusBadRequest, gin.H{"error": err.Error()})
		return
	}

	// Create command with primitive types
	cmd, err := command.NewRegisterIngredientCommand(
		request.Name,
		request.PackageQuantity,
		request.PackagePrice,
		request.PackageUnit,
	)
	if err != nil {
		c.JSON(http.StatusBadRequest, gin.H{"error": err.Error()})
		return
	}

	// Execute use case
	result, err := h.registerIngredientUseCase.Execute(cmd)
	if err != nil {
		c.JSON(http.StatusInternalServerError, gin.H{"error": err.Error()})
		return
	}

	c.JSON(http.StatusCreated, result)
}