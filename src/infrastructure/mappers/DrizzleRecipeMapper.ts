import type { SelectRecipe, SelectRecipeIngredient } from '../database/schema/index.js'
import { Recipe } from '@domain/entities/Recipe.js'
import { Id } from '@domain/valueobjects/Id.js'
import { Money } from '@domain/valueobjects/Money.js'
import { RecipeIngredient } from '@domain/valueobjects/RecipeIngredient.js'
import { Unit } from '@domain/valueobjects/Unit.js'

type DrizzleRecipeWithIngredients = SelectRecipe & {
  recipeIngredients: SelectRecipeIngredient[]
}

export class DrizzleRecipeMapper {
  static toDomain(dbRecipe: DrizzleRecipeWithIngredients): Recipe {
    const ingredients = dbRecipe.recipeIngredients.map(
      (dbIngredient) =>
        new RecipeIngredient(
          new Id(dbIngredient.ingredientId),
          dbIngredient.quantity,
          this.mapUnit(dbIngredient.unit)
        )
    )

    return new Recipe(
      new Id(dbRecipe.id),
      dbRecipe.name,
      ingredients,
      new Money(dbRecipe.totalCost)
    )
  }

  static toDatabase(recipe: Recipe): {
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

  static toRecipeIngredients(recipe: Recipe): {
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

  private static mapUnit(dbUnit: string): Unit {
    return dbUnit as Unit // String enum mapping
  }
}
