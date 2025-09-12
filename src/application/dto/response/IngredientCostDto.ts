import { Unit } from '@domain/valueobjects/Unit.js'

export interface IngredientCostDto {
  readonly ingredientId: string
  readonly ingredientName: string
  readonly unitCost: string
  readonly unit: Unit
  readonly totalCost: string
}