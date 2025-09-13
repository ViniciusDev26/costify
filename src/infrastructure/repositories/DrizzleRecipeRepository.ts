import type { RecipeRepository } from '@application/contracts/RecipeRepository.js'
import type { Recipe } from '@domain/entities/Recipe.js'
import type { Id } from '@domain/valueobjects/Id.js'
import { asc, eq } from 'drizzle-orm'
import type { Database } from '../database/connection.js'
import { recipeIngredients, recipes } from '../database/schema/index.js'
import { DrizzleRecipeMapper } from '../mappers/DrizzleRecipeMapper.js'

export class DrizzleRecipeRepository implements RecipeRepository {
  constructor(private readonly db: Database) {}

  async save(recipe: Recipe): Promise<void> {
    const recipeData = DrizzleRecipeMapper.toDatabase(recipe)
    const ingredientsData = DrizzleRecipeMapper.toRecipeIngredients(recipe)

    await this.db.transaction(async (tx) => {
      // Create recipe
      await tx.insert(recipes).values(recipeData)

      // Create recipe ingredients
      if (ingredientsData.length > 0) {
        await tx.insert(recipeIngredients).values(ingredientsData)
      }
    })
  }

  async findById(id: Id): Promise<Recipe | null> {
    const result = await this.db.query.recipes.findFirst({
      where: eq(recipes.id, id.getValue()),
      with: {
        recipeIngredients: true,
      },
    })

    if (!result) {
      return null
    }

    return DrizzleRecipeMapper.toDomain(result)
  }

  async findByName(name: string): Promise<Recipe | null> {
    const result = await this.db.query.recipes.findFirst({
      where: eq(recipes.name, name),
      with: {
        recipeIngredients: true,
      },
    })

    if (!result) {
      return null
    }

    return DrizzleRecipeMapper.toDomain(result)
  }

  async findAll(): Promise<Recipe[]> {
    const result = await this.db.query.recipes.findMany({
      with: {
        recipeIngredients: true,
      },
      orderBy: asc(recipes.name),
    })

    return result.map((recipe) => DrizzleRecipeMapper.toDomain(recipe))
  }

  async update(recipe: Recipe): Promise<void> {
    const recipeData = DrizzleRecipeMapper.toDatabase(recipe)
    const ingredientsData = DrizzleRecipeMapper.toRecipeIngredients(recipe)

    await this.db.transaction(async (tx) => {
      // Update recipe
      await tx
        .update(recipes)
        .set({
          name: recipeData.name,
          totalCost: recipeData.totalCost,
          updatedAt: new Date(),
        })
        .where(eq(recipes.id, recipe.getId().getValue()))

      // Delete existing ingredients
      await tx
        .delete(recipeIngredients)
        .where(eq(recipeIngredients.recipeId, recipe.getId().getValue()))

      // Create new ingredients
      if (ingredientsData.length > 0) {
        await tx.insert(recipeIngredients).values(ingredientsData)
      }
    })
  }

  async delete(id: Id): Promise<void> {
    await this.db.delete(recipes).where(eq(recipes.id, id.getValue()))
  }

  async existsById(id: Id): Promise<boolean> {
    const result = await this.db
      .select({ count: recipes.id })
      .from(recipes)
      .where(eq(recipes.id, id.getValue()))
      .limit(1)

    return result.length > 0
  }

  async existsByName(name: string): Promise<boolean> {
    const result = await this.db
      .select({ count: recipes.id })
      .from(recipes)
      .where(eq(recipes.name, name))
      .limit(1)

    return result.length > 0
  }
}
