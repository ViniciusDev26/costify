import { PrismaClient } from '@prisma/client'
import type { RecipeRepository } from '@application/contracts/RecipeRepository.js'
import { Recipe } from '@domain/entities/Recipe.js'
import { Id } from '@domain/valueobjects/Id.js'
import { PrismaRecipeMapper } from '../mappers/PrismaRecipeMapper.js'

export class PrismaRecipeRepository implements RecipeRepository {
  constructor(private readonly prisma: PrismaClient) {}

  async save(recipe: Recipe): Promise<void> {
    const recipeData = PrismaRecipeMapper.toPrisma(recipe)
    const ingredientsData = PrismaRecipeMapper.toPrismaIngredients(recipe)

    await this.prisma.$transaction(async (tx) => {
      // Create recipe
      await tx.recipe.create({
        data: recipeData,
      })

      // Create recipe ingredients
      if (ingredientsData.length > 0) {
        await tx.recipeIngredient.createMany({
          data: ingredientsData,
        })
      }
    })
  }

  async findById(id: Id): Promise<Recipe | null> {
    const prismaRecipe = await this.prisma.recipe.findUnique({
      where: { id: id.getValue() },
      include: { ingredients: true },
    })

    if (!prismaRecipe) {
      return null
    }

    return PrismaRecipeMapper.toDomain(prismaRecipe)
  }

  async findByName(name: string): Promise<Recipe | null> {
    const prismaRecipe = await this.prisma.recipe.findUnique({
      where: { name },
      include: { ingredients: true },
    })

    if (!prismaRecipe) {
      return null
    }

    return PrismaRecipeMapper.toDomain(prismaRecipe)
  }

  async findAll(): Promise<Recipe[]> {
    const prismaRecipes = await this.prisma.recipe.findMany({
      include: { ingredients: true },
      orderBy: { name: 'asc' },
    })

    return prismaRecipes.map(recipe => PrismaRecipeMapper.toDomain(recipe))
  }

  async update(recipe: Recipe): Promise<void> {
    const recipeData = PrismaRecipeMapper.toPrisma(recipe)
    const ingredientsData = PrismaRecipeMapper.toPrismaIngredients(recipe)

    await this.prisma.$transaction(async (tx) => {
      // Update recipe
      await tx.recipe.update({
        where: { id: recipe.getId().getValue() },
        data: {
          name: recipeData.name,
          totalCost: recipeData.totalCost,
        },
      })

      // Delete existing ingredients
      await tx.recipeIngredient.deleteMany({
        where: { recipeId: recipe.getId().getValue() },
      })

      // Create new ingredients
      if (ingredientsData.length > 0) {
        await tx.recipeIngredient.createMany({
          data: ingredientsData,
        })
      }
    })
  }

  async delete(id: Id): Promise<void> {
    await this.prisma.recipe.delete({
      where: { id: id.getValue() },
    })
  }

  async existsById(id: Id): Promise<boolean> {
    const count = await this.prisma.recipe.count({
      where: { id: id.getValue() },
    })
    return count > 0
  }

  async existsByName(name: string): Promise<boolean> {
    const count = await this.prisma.recipe.count({
      where: { name },
    })
    return count > 0
  }
}