package br.unifor.costify.domain.valueobject;

import java.util.Objects;

public final class IngredientCost {
    private final Id ingredientId;
    private final String ingredientName;
    private final double quantityUsed;
    private final Unit unit;
    private final double cost;

    public IngredientCost(Id ingredientId, String ingredientName, double quantityUsed, Unit unit, double cost) {
        if (ingredientId == null) {
            throw new IllegalArgumentException("Ingredient ID cannot be null");
        }
        if (ingredientName == null || ingredientName.isBlank()) {
            throw new IllegalArgumentException("Ingredient name cannot be null or empty");
        }
        if (quantityUsed <= 0) {
            throw new IllegalArgumentException("Quantity used must be greater than zero");
        }
        if (unit == null) {
            throw new IllegalArgumentException("Unit cannot be null");
        }
        if (cost < 0) {
            throw new IllegalArgumentException("Cost cannot be negative");
        }

        this.ingredientId = ingredientId;
        this.ingredientName = ingredientName;
        this.quantityUsed = quantityUsed;
        this.unit = unit;
        this.cost = cost;
    }

    public Id getIngredientId() {
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

    public double getCost() {
        return cost;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        IngredientCost that = (IngredientCost) o;
        return Double.compare(quantityUsed, that.quantityUsed) == 0 &&
                Double.compare(cost, that.cost) == 0 &&
                Objects.equals(ingredientId, that.ingredientId) &&
                Objects.equals(ingredientName, that.ingredientName) &&
                unit == that.unit;
    }

    @Override
    public int hashCode() {
        return Objects.hash(ingredientId, ingredientName, quantityUsed, unit, cost);
    }

    @Override
    public String toString() {
        return String.format("IngredientCost{ingredientId=%s, ingredientName='%s', quantityUsed=%.2f %s, cost=%.2f}", 
                ingredientId, ingredientName, quantityUsed, unit, cost);
    }
}