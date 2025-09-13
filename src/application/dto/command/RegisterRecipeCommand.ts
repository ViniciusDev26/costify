import type { Unit } from '@domain/valueobjects/Unit.js'

export interface RecipeIngredientCommand {
  readonly ingredientId: string
  readonly quantity: string | number
  readonly unit: Unit | string
}

export interface RegisterRecipeCommand {
  readonly name: string
  readonly ingredients: RecipeIngredientCommand[]
}
