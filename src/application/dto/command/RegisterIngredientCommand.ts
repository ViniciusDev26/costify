import { Unit } from '@domain/valueobjects/Unit.js'

export interface RegisterIngredientCommand {
  readonly name: string
  readonly pricePerUnit: string | number
  readonly unit: Unit | string
}