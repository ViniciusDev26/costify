import type { IDecimal } from '../contracts/DecimalProvider.js'
import { InvalidIngredientNameException } from '../errors/ingredient/InvalidIngredientNameException.js'
import type { Id } from '../valueobjects/Id.js'
import type { Money } from '../valueobjects/Money.js'
import type { Unit } from '../valueobjects/Unit.js'

export class Ingredient {
  private readonly id: Id
  private name: string
  private pricePerUnit: Money
  private unit: Unit

  constructor(id: Id, name: string, pricePerUnit: Money, unit: Unit) {
    this.validateName(name)

    this.id = id
    this.name = name.trim()
    this.pricePerUnit = pricePerUnit
    this.unit = unit
  }

  private validateName(name: string): void {
    if (!name || name.trim().length === 0) {
      throw new InvalidIngredientNameException(name || 'null')
    }

    if (name.trim().length > 255) {
      throw new InvalidIngredientNameException('Name too long (max 255 characters)')
    }
  }

  getId(): Id {
    return this.id
  }

  getName(): string {
    return this.name
  }

  getPricePerUnit(): Money {
    return this.pricePerUnit
  }

  getUnit(): Unit {
    return this.unit
  }

  updateName(newName: string): void {
    this.validateName(newName)
    this.name = newName.trim()
  }

  updatePricePerUnit(newPrice: Money): void {
    this.pricePerUnit = newPrice
  }

  updateUnit(newUnit: Unit): void {
    this.unit = newUnit
  }

  calculateCost(quantity: string | number | IDecimal): Money {
    return this.pricePerUnit.multiply(quantity)
  }

  equals(other: Ingredient): boolean {
    if (!(other instanceof Ingredient)) {
      return false
    }
    return this.id.equals(other.id)
  }

  toString(): string {
    return `Ingredient{id=${this.id.toString()}, name='${this.name}', pricePerUnit=${this.pricePerUnit.toString()}, unit=${this.unit}}`
  }
}
