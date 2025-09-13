import type { Ingredient } from '@domain/entities/Ingredient.js'
import type { IngredientDto } from '../dto/entity/IngredientDto.js'

export function toDto(ingredient: Ingredient): IngredientDto {
  return {
    id: ingredient.getId().getValue(),
    name: ingredient.getName(),
    pricePerUnit: ingredient.getPricePerUnit().toFixed(2),
    unit: ingredient.getUnit(),
  }
}

export function toDtoList(ingredients: Ingredient[]): IngredientDto[] {
  return ingredients.map((ingredient) => toDto(ingredient))
}

// Legacy compatibility
export const IngredientMapper = {
  toDto,
  toDtoList,
}
