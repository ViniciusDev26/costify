package br.unifor.costify.application.dto.response;

import br.unifor.costify.domain.valueobject.RecipeCost;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

public class RecipeCostDto {
    private final String recipeId;
    private final String recipeName;
    private final List<IngredientCostDto> ingredientCosts;
    private final BigDecimal totalCost;
    
    public RecipeCostDto(String recipeId, String recipeName, List<IngredientCostDto> ingredientCosts, BigDecimal totalCost) {
        this.recipeId = recipeId;
        this.recipeName = recipeName;
        this.ingredientCosts = ingredientCosts;
        this.totalCost = totalCost;
    }
    
    public static RecipeCostDto fromDomain(RecipeCost recipeCost) {
        List<IngredientCostDto> ingredientCostDtos = recipeCost.getIngredientCosts()
                .stream()
                .map(IngredientCostDto::fromDomain)
                .collect(Collectors.toList());
                
        return new RecipeCostDto(
                recipeCost.getRecipeId().getValue(),
                recipeCost.getRecipeName(),
                ingredientCostDtos,
                recipeCost.getTotalCost().getAmount()
        );
    }
    
    public String getRecipeId() {
        return recipeId;
    }
    
    public String getRecipeName() {
        return recipeName;
    }
    
    public List<IngredientCostDto> getIngredientCosts() {
        return ingredientCosts;
    }
    
    public BigDecimal getTotalCost() {
        return totalCost;
    }
}