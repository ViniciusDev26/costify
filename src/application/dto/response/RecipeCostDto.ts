import type { IngredientCostDto } from './IngredientCostDto.js'

export interface RecipeCostDto {
  readonly totalCost: string
  readonly ingredientCosts: IngredientCostDto[]
}
