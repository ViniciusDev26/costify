package br.unifor.costify.domain.entity;

import br.unifor.costify.domain.contracts.IdGenerator;
import br.unifor.costify.domain.errors.recipe.EmptyRecipeException;
import br.unifor.costify.domain.errors.recipe.InvalidTotalCostException;
import br.unifor.costify.domain.valueobject.Id;
import br.unifor.costify.domain.valueobject.Money;
import br.unifor.costify.domain.valueobject.RecipeIngredient;
import java.util.ArrayList;
import java.util.List;

public class Recipe {
  private Id id;
  private String name;
  private List<RecipeIngredient> ingredients;
  private Money totalCost;


  public Recipe(Id id, String name, List<RecipeIngredient> ingredients, Money totalCost) {
    this.validate(name, ingredients);
    if (totalCost == null) {
      throw new InvalidTotalCostException("Total cost cannot be null");
    }
    this.id = id;
    this.name = name;
    this.ingredients = new ArrayList<>(ingredients);
    this.totalCost = totalCost;
  }


  public Recipe(IdGenerator idGenerator, String name, List<RecipeIngredient> ingredients, Money totalCost) {
    this.validate(name, ingredients);
    if (totalCost == null) {
      throw new InvalidTotalCostException("Total cost cannot be null");
    }
    this.id = Id.generate(idGenerator);
    this.name = name;
    this.ingredients = new ArrayList<>(ingredients);
    this.totalCost = totalCost;
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

  public Money getTotalCost() {
    return totalCost;
  }


  public void addIngredient(RecipeIngredient ingredient) {
    this.ingredients.add(ingredient);
  }

  public void removeIngredient(Id ingredientId) {
    List<RecipeIngredient> updatedIngredients =
        ingredients.stream().filter(ri -> !ri.getIngredientId().equals(ingredientId)).toList();

    if (updatedIngredients.isEmpty()) {
      throw new EmptyRecipeException("Recipe must have at least one ingredient");
    }

    this.ingredients = new ArrayList<>(updatedIngredients);
  }

  public void updateTotalCost(Money newTotalCost) {
    if (newTotalCost == null) {
      throw new InvalidTotalCostException("Total cost cannot be null");
    }
    this.totalCost = newTotalCost;
  }

  public void updateName(String newName) {
    if (newName == null || newName.isBlank()) {
      throw new IllegalArgumentException("Recipe name cannot be null or empty");
    }
    this.name = newName;
  }

  public void updateIngredients(List<RecipeIngredient> newIngredients) {
    if (newIngredients == null || newIngredients.isEmpty()) {
      throw new EmptyRecipeException("Recipe must have at least one ingredient");
    }
    this.ingredients = new ArrayList<>(newIngredients);
  }

  private void validate(String name, List<RecipeIngredient> ingredients) {
    if (ingredients == null || ingredients.isEmpty()) {
      throw new EmptyRecipeException("Recipe must have at least one ingredient");
    }
  }
}
