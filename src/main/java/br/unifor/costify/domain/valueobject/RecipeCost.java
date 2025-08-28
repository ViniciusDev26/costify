package br.unifor.costify.domain.valueobject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public final class RecipeCost {
    private final Id recipeId;
    private final String recipeName;
    private final List<IngredientCost> ingredientCosts;
    private final double totalCost;

    public RecipeCost(Id recipeId, String recipeName, List<IngredientCost> ingredientCosts) {
        if (recipeId == null) {
            throw new IllegalArgumentException("Recipe ID cannot be null");
        }
        if (recipeName == null || recipeName.isBlank()) {
            throw new IllegalArgumentException("Recipe name cannot be null or empty");
        }
        if (ingredientCosts == null) {
            throw new IllegalArgumentException("Ingredient costs cannot be null");
        }
        if (ingredientCosts.isEmpty()) {
            throw new IllegalArgumentException("Recipe must have at least one ingredient cost");
        }

        this.recipeId = recipeId;
        this.recipeName = recipeName;
        this.ingredientCosts = new ArrayList<>(ingredientCosts);
        this.totalCost = ingredientCosts.stream()
                .mapToDouble(IngredientCost::getCost)
                .sum();
    }

    public Id getRecipeId() {
        return recipeId;
    }

    public String getRecipeName() {
        return recipeName;
    }

    public List<IngredientCost> getIngredientCosts() {
        return Collections.unmodifiableList(ingredientCosts);
    }

    public double getTotalCost() {
        return totalCost;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RecipeCost that = (RecipeCost) o;
        return Double.compare(totalCost, that.totalCost) == 0 &&
                Objects.equals(recipeId, that.recipeId) &&
                Objects.equals(recipeName, that.recipeName) &&
                Objects.equals(ingredientCosts, that.ingredientCosts);
    }

    @Override
    public int hashCode() {
        return Objects.hash(recipeId, recipeName, ingredientCosts, totalCost);
    }

    @Override
    public String toString() {
        return String.format("RecipeCost{recipeId=%s, recipeName='%s', totalCost=%.2f, ingredientCount=%d}", 
                recipeId, recipeName, totalCost, ingredientCosts.size());
    }
}