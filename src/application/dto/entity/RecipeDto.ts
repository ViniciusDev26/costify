import { Unit } from '@domain/valueobjects/Unit.js'

export interface RecipeIngredientDto {
  readonly ingredientId: string
  readonly quantity: string
  readonly unit: Unit
}

export interface RecipeDto {
  readonly id: string
  readonly name: string
  readonly ingredients: RecipeIngredientDto[]
  readonly totalCost: string
}
