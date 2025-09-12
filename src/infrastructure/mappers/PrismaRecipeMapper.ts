import type { 
  Recipe as PrismaRecipe, 
  RecipeIngredient as PrismaRecipeIngredient, 
  Unit as PrismaUnit 
} from '@prisma/client'
import { Recipe } from '@domain/entities/Recipe.js'
import { Id } from '@domain/valueobjects/Id.js'
import { Money } from '@domain/valueobjects/Money.js'
import { RecipeIngredient } from '@domain/valueobjects/RecipeIngredient.js'
import { Unit } from '@domain/valueobjects/Unit.js'

type PrismaRecipeWithIngredients = PrismaRecipe & {
  ingredients: PrismaRecipeIngredient[]
}

export class PrismaRecipeMapper {
  static toDomain(prismaRecipe: PrismaRecipeWithIngredients): Recipe {
    const ingredients = prismaRecipe.ingredients.map(prismaIngredient => 
      new RecipeIngredient(
        new Id(prismaIngredient.ingredientId),
        prismaIngredient.quantity.toString(),
        this.mapUnit(prismaIngredient.unit)
      )
    )

    return new Recipe(
      new Id(prismaRecipe.id),
      prismaRecipe.name,
      ingredients,
      new Money(prismaRecipe.totalCost.toString())
    )
  }

  static toPrisma(recipe: Recipe): Omit<PrismaRecipe, 'createdAt' | 'updatedAt'> {
    return {
      id: recipe.getId().getValue(),
      name: recipe.getName(),
      totalCost: recipe.getTotalCost().toNumber(),
    }
  }

  static toPrismaIngredients(recipe: Recipe): Omit<PrismaRecipeIngredient, 'id' | 'createdAt'>[] {
    return recipe.getIngredients().map(ingredient => ({
      recipeId: recipe.getId().getValue(),
      ingredientId: ingredient.getIngredientId().getValue(),
      quantity: ingredient.getQuantity().toNumber(),
      unit: this.mapUnitToPrisma(ingredient.getUnit()),
    }))
  }

  private static mapUnit(prismaUnit: PrismaUnit): Unit {
    return prismaUnit as Unit // Enums are identical
  }

  private static mapUnitToPrisma(unit: Unit): PrismaUnit {
    return unit as PrismaUnit // Enums are identical
  }
}