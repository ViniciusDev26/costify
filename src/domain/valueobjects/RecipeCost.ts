import { Money } from './Money.js'
import { IngredientCost } from './IngredientCost.js'

export class RecipeCost {
  private readonly totalCost: Money
  private readonly ingredientCosts: readonly IngredientCost[]

  constructor(totalCost: Money, ingredientCosts: IngredientCost[]) {
    if (ingredientCosts.length === 0) {
      throw new Error('Recipe cost must have at least one ingredient cost')
    }

    // Validate that total cost matches sum of ingredient costs
    const calculatedTotal = ingredientCosts.reduce(
      (sum, ingredientCost) => sum.add(ingredientCost.getTotalCost()),
      Money.zero()
    )

    if (!totalCost.equals(calculatedTotal)) {
      throw new Error('Total cost does not match sum of ingredient costs')
    }

    this.totalCost = totalCost
    this.ingredientCosts = [...ingredientCosts] // Create defensive copy
  }

  getTotalCost(): Money {
    return this.totalCost
  }

  getIngredientCosts(): readonly IngredientCost[] {
    return this.ingredientCosts
  }

  getIngredientCostById(ingredientId: string): IngredientCost | undefined {
    return this.ingredientCosts.find(cost => 
      cost.getIngredientId().getValue() === ingredientId
    )
  }

  getIngredientCount(): number {
    return this.ingredientCosts.length
  }

  equals(other: RecipeCost): boolean {
    if (!(other instanceof RecipeCost)) {
      return false
    }

    if (!this.totalCost.equals(other.totalCost)) {
      return false
    }

    if (this.ingredientCosts.length !== other.ingredientCosts.length) {
      return false
    }

    return this.ingredientCosts.every((cost, index) => 
      cost.equals(other.ingredientCosts[index])
    )
  }

  toString(): string {
    const ingredientLines = this.ingredientCosts
      .map(cost => `  - ${cost.toString()}`)
      .join('\n')
    
    return `Recipe Cost: ${this.totalCost.toFixed(2)}\nIngredients:\n${ingredientLines}`
  }
}