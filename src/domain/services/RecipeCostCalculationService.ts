import type { DecimalProvider } from '../contracts/DecimalProvider.js'
import type { Ingredient } from '../entities/Ingredient.js'
import type { Recipe } from '../entities/Recipe.js'
import { IngredientCost } from '../valueobjects/IngredientCost.js'
import { Money } from '../valueobjects/Money.js'
import { RecipeCost } from '../valueobjects/RecipeCost.js'
import { canConvert, convert } from '../valueobjects/Unit.js'

export class RecipeCostCalculationService {
  constructor(private readonly decimalProvider: DecimalProvider) {}

  calculateRecipeCost(recipe: Recipe, ingredients: Ingredient[]): RecipeCost {
    if (recipe.getIngredientCount() === 0) {
      throw new Error('Cannot calculate cost for empty recipe')
    }

    const ingredientCosts: IngredientCost[] = []
    let totalCost = Money.zero()

    for (const recipeIngredient of recipe.getIngredients()) {
      const ingredient = ingredients.find((ing) =>
        ing.getId().equals(recipeIngredient.getIngredientId())
      )

      if (!ingredient) {
        throw new Error(`Ingredient not found: ${recipeIngredient.getIngredientId().getValue()}`)
      }

      // Check if units can be converted, otherwise throw error
      if (!canConvert(recipeIngredient.getUnit(), ingredient.getUnit())) {
        throw new Error(
          `Incompatible unit types for ingredient ${ingredient.getName()}: ` +
            `recipe requires ${recipeIngredient.getUnit()}, but ingredient is priced per ${ingredient.getUnit()}`
        )
      }

      // Convert recipe quantity to ingredient's unit for cost calculation using precise decimal arithmetic
      const recipeQuantityDecimal = this.decimalProvider.create(recipeIngredient.getQuantity())
      const convertedQuantityDecimal = convert(
        recipeQuantityDecimal,
        recipeIngredient.getUnit(),
        ingredient.getUnit(),
        this.decimalProvider
      )

      const ingredientTotalCost = ingredient.calculateCost(convertedQuantityDecimal)

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
