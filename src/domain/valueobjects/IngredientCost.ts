import { Id } from './Id.js'
import { Money } from './Money.js'
import { Unit } from './Unit.js'

export class IngredientCost {
  private readonly ingredientId: Id
  private readonly ingredientName: string
  private readonly unitCost: Money
  private readonly unit: Unit
  private readonly totalCost: Money

  constructor(
    ingredientId: Id,
    ingredientName: string,
    unitCost: Money,
    unit: Unit,
    totalCost: Money
  ) {
    if (!ingredientName || ingredientName.trim().length === 0) {
      throw new Error('Ingredient name cannot be empty')
    }

    this.ingredientId = ingredientId
    this.ingredientName = ingredientName.trim()
    this.unitCost = unitCost
    this.unit = unit
    this.totalCost = totalCost
  }

  getIngredientId(): Id {
    return this.ingredientId
  }

  getIngredientName(): string {
    return this.ingredientName
  }

  getUnitCost(): Money {
    return this.unitCost
  }

  getUnit(): Unit {
    return this.unit
  }

  getTotalCost(): Money {
    return this.totalCost
  }

  equals(other: IngredientCost): boolean {
    if (!(other instanceof IngredientCost)) {
      return false
    }

    return (
      this.ingredientId.equals(other.ingredientId) &&
      this.ingredientName === other.ingredientName &&
      this.unitCost.equals(other.unitCost) &&
      this.unit === other.unit &&
      this.totalCost.equals(other.totalCost)
    )
  }

  toString(): string {
    return `${this.ingredientName}: ${this.totalCost.toFixed(2)} (${this.unitCost.toFixed(2)} per ${this.unit})`
  }
}
