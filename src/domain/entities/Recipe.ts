import { EmptyRecipeException } from '../errors/recipe/EmptyRecipeException.js'
import { InvalidQuantityException } from '../errors/recipe/InvalidQuantityException.js'
import type { Id } from '../valueobjects/Id.js'
import type { Money } from '../valueobjects/Money.js'
import { RecipeIngredient } from '../valueobjects/RecipeIngredient.js'

export class Recipe {
  private readonly id: Id
  private name: string
  private ingredients: RecipeIngredient[]
  private totalCost: Money

  constructor(id: Id, name: string, ingredients: RecipeIngredient[], totalCost: Money) {
    this.validateName(name)
    this.validateIngredients(ingredients)

    this.id = id
    this.name = name.trim()
    this.ingredients = [...ingredients] // Defensive copy
    this.totalCost = totalCost
  }

  private validateName(name: string): void {
    if (!name || name.trim().length === 0) {
      throw new Error('Recipe name cannot be empty')
    }

    if (name.trim().length > 255) {
      throw new Error('Recipe name too long (max 255 characters)')
    }
  }

  private validateIngredients(ingredients: RecipeIngredient[]): void {
    if (!ingredients || ingredients.length === 0) {
      throw new EmptyRecipeException()
    }

    // Check for duplicate ingredients
    const ingredientIds = new Set<string>()
    for (const ingredient of ingredients) {
      const idValue = ingredient.getIngredientId().getValue()
      if (ingredientIds.has(idValue)) {
        throw new Error(`Duplicate ingredient found: ${idValue}`)
      }
      ingredientIds.add(idValue)

      // Validate quantity
      if (ingredient.getQuantity().isNegative() || ingredient.getQuantity().isZero()) {
        throw new InvalidQuantityException(ingredient.getQuantity().toString())
      }
    }
  }

  getId(): Id {
    return this.id
  }

  getName(): string {
    return this.name
  }

  getIngredients(): readonly RecipeIngredient[] {
    return [...this.ingredients] // Return defensive copy
  }

  getTotalCost(): Money {
    return this.totalCost
  }

  updateName(newName: string): void {
    this.validateName(newName)
    this.name = newName.trim()
  }

  addIngredient(ingredient: RecipeIngredient): void {
    // Check if ingredient already exists
    const existingIngredient = this.ingredients.find((ing) =>
      ing.getIngredientId().equals(ingredient.getIngredientId())
    )

    if (existingIngredient) {
      throw new Error(
        `Ingredient ${ingredient.getIngredientId().getValue()} already exists in recipe`
      )
    }

    this.ingredients.push(ingredient)
  }

  removeIngredient(ingredientId: Id): void {
    const index = this.ingredients.findIndex((ing) => ing.getIngredientId().equals(ingredientId))

    if (index === -1) {
      throw new Error(`Ingredient ${ingredientId.getValue()} not found in recipe`)
    }

    this.ingredients.splice(index, 1)

    if (this.ingredients.length === 0) {
      throw new EmptyRecipeException()
    }
  }

  updateIngredientQuantity(ingredientId: Id, newQuantity: string | number): void {
    const ingredient = this.ingredients.find((ing) => ing.getIngredientId().equals(ingredientId))

    if (!ingredient) {
      throw new Error(`Ingredient ${ingredientId.getValue()} not found in recipe`)
    }

    // Create new ingredient with updated quantity
    const updatedIngredient = new RecipeIngredient(
      ingredient.getIngredientId(),
      newQuantity,
      ingredient.getUnit()
    )

    // Replace the ingredient
    const index = this.ingredients.findIndex((ing) => ing.getIngredientId().equals(ingredientId))
    this.ingredients[index] = updatedIngredient
  }

  updateTotalCost(newTotalCost: Money): void {
    this.totalCost = newTotalCost
  }

  getIngredientCount(): number {
    return this.ingredients.length
  }

  hasIngredient(ingredientId: Id): boolean {
    return this.ingredients.some((ing) => ing.getIngredientId().equals(ingredientId))
  }

  equals(other: Recipe): boolean {
    if (!(other instanceof Recipe)) {
      return false
    }
    return this.id.equals(other.id)
  }

  toString(): string {
    return `Recipe{id=${this.id.toString()}, name='${this.name}', ingredients=${this.ingredients.length}, totalCost=${this.totalCost.toString()}}`
  }
}
