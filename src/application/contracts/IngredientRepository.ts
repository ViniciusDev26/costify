import { Ingredient } from '@domain/entities/Ingredient.js'
import { Id } from '@domain/valueobjects/Id.js'

export interface IngredientRepository {
  save(ingredient: Ingredient): Promise<void>
  findById(id: Id): Promise<Ingredient | null>
  findByName(name: string): Promise<Ingredient | null>
  findAll(): Promise<Ingredient[]>
  update(ingredient: Ingredient): Promise<void>
  delete(id: Id): Promise<void>
  existsById(id: Id): Promise<boolean>
  existsByName(name: string): Promise<boolean>
}
