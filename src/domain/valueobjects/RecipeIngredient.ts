import type { IDecimal, DecimalProvider } from '../contracts/DecimalProvider.js'
import type { Id } from './Id.js'
import type { Unit } from './Unit.js'

export class RecipeIngredient {
  private readonly ingredientId: Id
  private readonly quantity: IDecimal
  private readonly unit: Unit
  private static decimalProvider: DecimalProvider

  constructor(ingredientId: Id, quantity: string | number | IDecimal, unit: Unit) {
    if (!RecipeIngredient.decimalProvider) {
      throw new Error('DecimalProvider must be configured before using RecipeIngredient')
    }

    const quantityDecimal =
      typeof quantity === 'object' && 'toNumber' in quantity
        ? quantity
        : RecipeIngredient.decimalProvider.create(quantity)

    if (quantityDecimal.isNegative() || quantityDecimal.isZero()) {
      throw new Error('Recipe ingredient quantity must be positive')
    }

    this.ingredientId = ingredientId
    this.quantity = quantityDecimal
    this.unit = unit
  }

  static configure(decimalProvider: DecimalProvider): void {
    RecipeIngredient.decimalProvider = decimalProvider
  }

  getIngredientId(): Id {
    return this.ingredientId
  }

  getQuantity(): IDecimal {
    return this.quantity
  }

  getQuantityAsNumber(): number {
    return this.quantity.toNumber()
  }

  getUnit(): Unit {
    return this.unit
  }

  equals(other: RecipeIngredient): boolean {
    if (!(other instanceof RecipeIngredient)) {
      return false
    }

    return (
      this.ingredientId.equals(other.ingredientId) &&
      this.quantity.equals(other.quantity) &&
      this.unit === other.unit
    )
  }

  toString(): string {
    return `${this.quantity.toString()} ${this.unit} of ingredient ${this.ingredientId.toString()}`
  }
}
