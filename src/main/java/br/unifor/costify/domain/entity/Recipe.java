package br.unifor.costify.domain.entity;

import br.unifor.costify.domain.contracts.IdGenerator;
import br.unifor.costify.domain.valueobject.Id;
import br.unifor.costify.domain.valueobject.RecipeIngredient;
import java.util.ArrayList;
import java.util.List;
import br.unifor.costify.domain.errors.recipe.EmptyRecipeException;

public class Recipe {
  private Id id;
  private String name;
  private List<RecipeIngredient> ingredients;

  public Recipe(Id id, String name, List<RecipeIngredient> ingredients) {
    this.validate(name, ingredients);
    this.id = id;
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

  private void validate(String name, List<RecipeIngredient> ingredients) {
    if (ingredients == null || ingredients.isEmpty()) {
      throw new EmptyRecipeException("Recipe must have at least one ingredient");
    }
  }
}
