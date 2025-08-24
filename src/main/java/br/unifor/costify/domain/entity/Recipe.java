package br.unifor.costify.domain.entity;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import br.unifor.costify.domain.contracts.IdGenerator;
import br.unifor.costify.domain.valueobject.Id;
import br.unifor.costify.domain.valueobject.RecipeIngredient;

public class Recipe {
    private Id id;
    private String name;
    private List<RecipeIngredient> ingredients;

    public Recipe(Id id, String name, List<RecipeIngredient> ingredients) {
        this.id = Objects.requireNonNull(id, "Id cannot be null");
        this.validate(name, ingredients);
        this.name = name;
        this.ingredients = new ArrayList<>(ingredients);
    }

    public Recipe(IdGenerator idGenerator, String name, List<RecipeIngredient> ingredients) {
        this.validate(name, ingredients);
        this.id = Id.generate(idGenerator);
        this.name = name;
        this.ingredients = new ArrayList<>(ingredients);
    }

    public Id getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public List<RecipeIngredient> getIngredients() {
        return new ArrayList<>(ingredients);
    }

    public void setName(String name) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Recipe name cannot be null or empty");
        }
        this.name = name;
    }

    public void setIngredients(List<RecipeIngredient> ingredients) {
        if (ingredients == null || ingredients.isEmpty()) {
            throw new IllegalArgumentException("Recipe must have at least one ingredient");
        }
        this.ingredients = new ArrayList<>(ingredients);
    }

    public void addIngredient(RecipeIngredient ingredient) {
        Objects.requireNonNull(ingredient, "Ingredient cannot be null");
        this.ingredients.add(ingredient);
    }

    public void removeIngredient(Id ingredientId) {
        Objects.requireNonNull(ingredientId, "Ingredient ID cannot be null");
        
        List<RecipeIngredient> updatedIngredients = ingredients.stream()
            .filter(ri -> !ri.getIngredientId().equals(ingredientId))
            .toList();
            
        if (updatedIngredients.isEmpty()) {
            throw new IllegalArgumentException("Cannot remove ingredient - recipe must have at least one ingredient");
        }
        
        this.ingredients = new ArrayList<>(updatedIngredients);
    }

    private void validate(String name, List<RecipeIngredient> ingredients) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Recipe name cannot be null or empty");
        }
        if (ingredients == null || ingredients.isEmpty()) {
            throw new IllegalArgumentException("Recipe must have at least one ingredient");
        }
    }
}