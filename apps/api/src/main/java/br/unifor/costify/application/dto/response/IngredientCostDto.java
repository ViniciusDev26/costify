package br.unifor.costify.application.dto.response;

import br.unifor.costify.domain.valueobject.IngredientCost;
import br.unifor.costify.domain.valueobject.Unit;

import java.math.BigDecimal;

public class IngredientCostDto {
    private final String ingredientId;
    private final String ingredientName;
    private final double quantityUsed;
    private final Unit unit;
    private final BigDecimal cost;
    
    public IngredientCostDto(String ingredientId, String ingredientName, double quantityUsed, Unit unit, BigDecimal cost) {
        this.ingredientId = ingredientId;
        this.ingredientName = ingredientName;
        this.quantityUsed = quantityUsed;
        this.unit = unit;
        this.cost = cost;
    }
    
    public static IngredientCostDto fromDomain(IngredientCost ingredientCost) {
        return new IngredientCostDto(
                ingredientCost.getIngredientId().getValue(),
                ingredientCost.getIngredientName(),
                ingredientCost.getQuantityUsed(),
                ingredientCost.getUnit(),
                ingredientCost.getCost().getAmount()
        );
    }
    
    public String getIngredientId() {
        return ingredientId;
    }
    
    public String getIngredientName() {
        return ingredientName;
    }
    
    public double getQuantityUsed() {
        return quantityUsed;
    }
    
    public Unit getUnit() {
        return unit;
    }
    
    public BigDecimal getCost() {
        return cost;
    }
}