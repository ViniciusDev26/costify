import { eq, asc } from 'drizzle-orm'
import type { Database } from '../database/connection.js'
import { ingredients } from '../database/schema/index.js'
import type { IngredientRepository } from '@application/contracts/IngredientRepository.js'
import { Ingredient } from '@domain/entities/Ingredient.js'
import { Id } from '@domain/valueobjects/Id.js'
import { DrizzleIngredientMapper } from '../mappers/DrizzleIngredientMapper.js'

export class DrizzleIngredientRepository implements IngredientRepository {
  constructor(private readonly db: Database) {}

  async save(ingredient: Ingredient): Promise<void> {
    const dbData = DrizzleIngredientMapper.toDatabase(ingredient)
    await this.db.insert(ingredients).values(dbData)
  }

  async findById(id: Id): Promise<Ingredient | null> {
    const result = await this.db
      .select()
      .from(ingredients)
      .where(eq(ingredients.id, id.getValue()))
      .limit(1)

    if (result.length === 0) {
      return null
    }

    return DrizzleIngredientMapper.toDomain(result[0])
  }

  async findByName(name: string): Promise<Ingredient | null> {
    const result = await this.db
      .select()
      .from(ingredients)
      .where(eq(ingredients.name, name))
      .limit(1)

    if (result.length === 0) {
      return null
    }

    return DrizzleIngredientMapper.toDomain(result[0])
  }

  async findAll(): Promise<Ingredient[]> {
    const result = await this.db.select().from(ingredients).orderBy(asc(ingredients.name))

    return result.map((ingredient) => DrizzleIngredientMapper.toDomain(ingredient))
  }

  async update(ingredient: Ingredient): Promise<void> {
    const dbData = DrizzleIngredientMapper.toDatabase(ingredient)
    await this.db
      .update(ingredients)
      .set({
        name: dbData.name,
        pricePerUnit: dbData.pricePerUnit,
        unit: dbData.unit,
        updatedAt: new Date(),
      })
      .where(eq(ingredients.id, ingredient.getId().getValue()))
  }

  async delete(id: Id): Promise<void> {
    await this.db.delete(ingredients).where(eq(ingredients.id, id.getValue()))
  }

  async existsById(id: Id): Promise<boolean> {
    const result = await this.db
      .select({ count: ingredients.id })
      .from(ingredients)
      .where(eq(ingredients.id, id.getValue()))
      .limit(1)

    return result.length > 0
  }

  async existsByName(name: string): Promise<boolean> {
    const result = await this.db
      .select({ count: ingredients.id })
      .from(ingredients)
      .where(eq(ingredients.name, name))
      .limit(1)

    return result.length > 0
  }
}
