import { PrismaClient } from '@prisma/client'
import type { IngredientRepository } from '@application/contracts/IngredientRepository.js'
import { Ingredient } from '@domain/entities/Ingredient.js'
import { Id } from '@domain/valueobjects/Id.js'
import { PrismaIngredientMapper } from '../mappers/PrismaIngredientMapper.js'

export class PrismaIngredientRepository implements IngredientRepository {
  constructor(private readonly prisma: PrismaClient) {}

  async save(ingredient: Ingredient): Promise<void> {
    const prismaData = PrismaIngredientMapper.toPrisma(ingredient)
    await this.prisma.ingredient.create({
      data: prismaData,
    })
  }

  async findById(id: Id): Promise<Ingredient | null> {
    const prismaIngredient = await this.prisma.ingredient.findUnique({
      where: { id: id.getValue() },
    })

    if (!prismaIngredient) {
      return null
    }

    return PrismaIngredientMapper.toDomain(prismaIngredient)
  }

  async findByName(name: string): Promise<Ingredient | null> {
    const prismaIngredient = await this.prisma.ingredient.findUnique({
      where: { name },
    })

    if (!prismaIngredient) {
      return null
    }

    return PrismaIngredientMapper.toDomain(prismaIngredient)
  }

  async findAll(): Promise<Ingredient[]> {
    const prismaIngredients = await this.prisma.ingredient.findMany({
      orderBy: { name: 'asc' },
    })

    return prismaIngredients.map(ingredient => PrismaIngredientMapper.toDomain(ingredient))
  }

  async update(ingredient: Ingredient): Promise<void> {
    const prismaData = PrismaIngredientMapper.toPrisma(ingredient)
    await this.prisma.ingredient.update({
      where: { id: ingredient.getId().getValue() },
      data: {
        name: prismaData.name,
        pricePerUnit: prismaData.pricePerUnit,
        unit: prismaData.unit,
      },
    })
  }

  async delete(id: Id): Promise<void> {
    await this.prisma.ingredient.delete({
      where: { id: id.getValue() },
    })
  }

  async existsById(id: Id): Promise<boolean> {
    const count = await this.prisma.ingredient.count({
      where: { id: id.getValue() },
    })
    return count > 0
  }

  async existsByName(name: string): Promise<boolean> {
    const count = await this.prisma.ingredient.count({
      where: { name },
    })
    return count > 0
  }
}