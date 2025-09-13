import type { Recipe } from '@domain/entities/Recipe.js'
import type { IngredientCost } from '@domain/valueobjects/IngredientCost.js'
import type { RecipeCost } from '@domain/valueobjects/RecipeCost.js'
import type { RecipeDto } from '../dto/entity/RecipeDto.js'
import type { IngredientCostDto } from '../dto/response/IngredientCostDto.js'
import type { RecipeCostDto } from '../dto/response/RecipeCostDto.js'

export function toDto(recipe: Recipe): RecipeDto {
  return {
    id: recipe.getId().getValue(),
    name: recipe.getName(),
    ingredients: recipe.getIngredients().map((ingredient) => ({
      ingredientId: ingredient.getIngredientId().getValue(),
      quantity: ingredient.getQuantity().toString(),
      unit: ingredient.getUnit(),
    })),
    totalCost: recipe.getTotalCost().toFixed(2),
  }
}

export function toDtoList(recipes: Recipe[]): RecipeDto[] {
  return recipes.map((recipe) => toDto(recipe))
}

export function recipeCostToDto(recipeCost: RecipeCost): RecipeCostDto {
  return {
    totalCost: recipeCost.getTotalCost().toFixed(2),
    ingredientCosts: recipeCost.getIngredientCosts().map((cost) => ingredientCostToDto(cost)),
  }
}

export function ingredientCostToDto(ingredientCost: IngredientCost): IngredientCostDto {
  return {
    ingredientId: ingredientCost.getIngredientId().getValue(),
    ingredientName: ingredientCost.getIngredientName(),
    unitCost: ingredientCost.getUnitCost().toFixed(2),
    unit: ingredientCost.getUnit(),
    totalCost: ingredientCost.getTotalCost().toFixed(2),
  }
}

// Legacy compatibility
export const RecipeMapper = {
  toDto,
  toDtoList,
  recipeCostToDto,
  ingredientCostToDto,
}
