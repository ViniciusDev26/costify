import { Unit } from '@domain/valueobjects/Unit.js'

export interface UpdateIngredientCommand {
  readonly id: string
  readonly name?: string
  readonly pricePerUnit?: string | number
  readonly unit?: Unit | string
}
