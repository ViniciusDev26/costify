package br.unifor.costify.application.validation;

import br.unifor.costify.domain.valueobject.Id;
import br.unifor.costify.domain.valueobject.RecipeIngredient;
import br.unifor.costify.domain.valueobject.Unit;

import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class ValidationService {
    
    public void validateIngredientData(String name, double packageQuantity, double packagePrice, Unit packageUnit) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Ingredient name cannot be null or empty");
        }
        if (packagePrice < 0) {
            throw new IllegalArgumentException("Package price cannot be negative");
        }
        if (packageUnit == null) {
            throw new IllegalArgumentException("Package unit cannot be null");
        }
    }
    
    public void validateRecipeData(String name, List<RecipeIngredient> ingredients) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Recipe name cannot be null or empty");
        }
        if (ingredients == null) {
            throw new IllegalArgumentException("Ingredients list cannot be null");
        }
    }
    
    public void validateId(Id id) {
        if (id == null) {
            throw new IllegalArgumentException("Id cannot be null");
        }
        if (id.getValue() == null || id.getValue().isBlank()) {
            throw new IllegalArgumentException("Id value cannot be null or empty");
        }
    }
    
    public void validateRecipeIngredientData(Id ingredientId, double quantity, Unit unit) {
        if (ingredientId == null) {
            throw new IllegalArgumentException("Ingredient ID cannot be null");
        }
        if (unit == null) {
            throw new IllegalArgumentException("Unit cannot be null");
        }
    }
    
    public void validateMoneyData(String amountStr) {
        if (amountStr == null || amountStr.isBlank()) {
            throw new IllegalArgumentException("Money amount cannot be null or empty");
        }
    }
}