import { Unit } from '@domain/valueobjects/Unit.js'

export interface IngredientDto {
  readonly id: string
  readonly name: string
  readonly pricePerUnit: string
  readonly unit: Unit
}
