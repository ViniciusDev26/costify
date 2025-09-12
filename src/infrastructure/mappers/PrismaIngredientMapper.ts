import type { Ingredient as PrismaIngredient, Unit as PrismaUnit } from '@prisma/client'
import { Ingredient } from '@domain/entities/Ingredient.js'
import { Id } from '@domain/valueobjects/Id.js'
import { Money } from '@domain/valueobjects/Money.js'
import { Unit } from '@domain/valueobjects/Unit.js'

export class PrismaIngredientMapper {
  static toDomain(prismaIngredient: PrismaIngredient): Ingredient {
    return new Ingredient(
      new Id(prismaIngredient.id),
      prismaIngredient.name,
      new Money(prismaIngredient.pricePerUnit.toString()),
      this.mapUnit(prismaIngredient.unit)
    )
  }

  static toPrisma(ingredient: Ingredient): Omit<PrismaIngredient, 'createdAt' | 'updatedAt'> {
    return {
      id: ingredient.getId().getValue(),
      name: ingredient.getName(),
      pricePerUnit: ingredient.getPricePerUnit().toNumber(),
      unit: this.mapUnitToPrisma(ingredient.getUnit()),
    }
  }

  private static mapUnit(prismaUnit: PrismaUnit): Unit {
    return prismaUnit as Unit // Enums are identical
  }

  private static mapUnitToPrisma(unit: Unit): PrismaUnit {
    return unit as PrismaUnit // Enums are identical
  }
}