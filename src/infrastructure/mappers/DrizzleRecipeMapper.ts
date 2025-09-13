import { Recipe } from '@domain/entities/Recipe.js'
import { Id } from '@domain/valueobjects/Id.js'
import { Money } from '@domain/valueobjects/Money.js'
import { RecipeIngredient } from '@domain/valueobjects/RecipeIngredient.js'
import type { Unit } from '@domain/valueobjects/Unit.js'
import type { SelectRecipe, SelectRecipeIngredient } from '../database/schema/index.js'

type DrizzleRecipeWithIngredients = SelectRecipe & {
  recipeIngredients: SelectRecipeIngredient[]
}

function mapUnit(dbUnit: string): Unit {
  return dbUnit as Unit // String enum mapping
}

export function toDomain(dbRecipe: DrizzleRecipeWithIngredients): Recipe {
  const ingredients = dbRecipe.recipeIngredients.map(
    (dbIngredient) =>
      new RecipeIngredient(
        new Id(dbIngredient.ingredientId),
        dbIngredient.quantity,
        mapUnit(dbIngredient.unit)
      )
  )

  return new Recipe(new Id(dbRecipe.id), dbRecipe.name, ingredients, new Money(dbRecipe.totalCost))
}

export function toDatabase(recipe: Recipe): {
  id: string
  name: string
  totalCost: string
} {
  return {
    id: recipe.getId().getValue(),
    name: recipe.getName(),
    totalCost: recipe.getTotalCost().toString(),
  }
}

export function toRecipeIngredients(recipe: Recipe): {
  recipeId: string
  ingredientId: string
  quantity: string
  unit: string
}[] {
  return recipe.getIngredients().map((ingredient) => ({
    recipeId: recipe.getId().getValue(),
    ingredientId: ingredient.getIngredientId().getValue(),
    quantity: ingredient.getQuantity().toString(),
    unit: ingredient.getUnit(),
  }))
}

// Legacy compatibility
export const DrizzleRecipeMapper = {
  toDomain,
  toDatabase,
  toRecipeIngredients,
}
