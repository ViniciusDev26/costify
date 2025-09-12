import { Recipe } from '@domain/entities/Recipe.js'
import { Id } from '@domain/valueobjects/Id.js'

export interface RecipeRepository {
  save(recipe: Recipe): Promise<void>
  findById(id: Id): Promise<Recipe | null>
  findByName(name: string): Promise<Recipe | null>
  findAll(): Promise<Recipe[]>
  update(recipe: Recipe): Promise<void>
  delete(id: Id): Promise<void>
  existsById(id: Id): Promise<boolean>
  existsByName(name: string): Promise<boolean>
}
