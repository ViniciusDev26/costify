import { Ingredient } from '@domain/entities/Ingredient.js'
import { Id } from '@domain/valueobjects/Id.js'
import { Money } from '@domain/valueobjects/Money.js'
import type { Unit } from '@domain/valueobjects/Unit.js'
import type { SelectIngredient } from '../database/schema/index.js'

function mapUnit(dbUnit: string): Unit {
  return dbUnit as Unit // String enum mapping
}

export function toDomain(dbIngredient: SelectIngredient): Ingredient {
  return new Ingredient(
    new Id(dbIngredient.id),
    dbIngredient.name,
    new Money(dbIngredient.pricePerUnit),
    mapUnit(dbIngredient.unit)
  )
}

export function toDatabase(ingredient: Ingredient): {
  id: string
  name: string
  pricePerUnit: string
  unit: string
} {
  return {
    id: ingredient.getId().getValue(),
    name: ingredient.getName(),
    pricePerUnit: ingredient.getPricePerUnit().toString(),
    unit: ingredient.getUnit(),
  }
}

// Legacy compatibility
export const DrizzleIngredientMapper = {
  toDomain,
  toDatabase,
}
