import { Recipe } from '../entities/Recipe.js'
import { Ingredient } from '../entities/Ingredient.js'
import { Money } from '../valueobjects/Money.js'
import { RecipeCost } from '../valueobjects/RecipeCost.js'
import { IngredientCost } from '../valueobjects/IngredientCost.js'

export class RecipeCostCalculationService {
  calculateRecipeCost(recipe: Recipe, ingredients: Ingredient[]): RecipeCost {
    if (recipe.getIngredientCount() === 0) {
      throw new Error('Cannot calculate cost for empty recipe')
    }

    const ingredientCosts: IngredientCost[] = []
    let totalCost = Money.zero()

    for (const recipeIngredient of recipe.getIngredients()) {
      const ingredient = ingredients.find(ing => 
        ing.getId().equals(recipeIngredient.getIngredientId())
      )

      if (!ingredient) {
        throw new Error(`Ingredient not found: ${recipeIngredient.getIngredientId().getValue()}`)
      }

      // Validate that units match
      if (ingredient.getUnit() !== recipeIngredient.getUnit()) {
        throw new Error(
          `Unit mismatch for ingredient ${ingredient.getName()}: ` +
          `recipe requires ${recipeIngredient.getUnit()}, but ingredient is priced per ${ingredient.getUnit()}`
        )
      }

      const ingredientTotalCost = ingredient.calculateCost(recipeIngredient.getQuantity())
      
      const ingredientCost = new IngredientCost(
        ingredient.getId(),
        ingredient.getName(),
        ingredient.getPricePerUnit(),
        ingredient.getUnit(),
        ingredientTotalCost
      )

      ingredientCosts.push(ingredientCost)
      totalCost = totalCost.add(ingredientTotalCost)
    }

    return new RecipeCost(totalCost, ingredientCosts)
  }

  calculateIngredientCost(ingredient: Ingredient, quantity: string | number): Money {
    return ingredient.calculateCost(quantity)
  }
}