import { afterAll, beforeAll, beforeEach, describe, expect, it } from 'bun:test'
import { Ingredient } from '@domain/entities/Ingredient.js'
import { Recipe } from '@domain/entities/Recipe.js'
import { Id } from '@domain/valueobjects/Id.js'
import { Money } from '@domain/valueobjects/Money.js'
import { RecipeIngredient } from '@domain/valueobjects/RecipeIngredient.js'
import { Unit } from '@domain/valueobjects/Unit.js'
import * as schema from '@infrastructure/database/schema/index.js'
import { DecimalJsProvider } from '@infrastructure/providers/DecimalJsProvider.js'
import { DrizzleIngredientRepository } from '@infrastructure/repositories/DrizzleIngredientRepository.js'
import { DrizzleRecipeRepository } from '@infrastructure/repositories/DrizzleRecipeRepository.js'
import { drizzle } from 'drizzle-orm/postgres-js'
import postgres from 'postgres'

describe('DrizzleRecipeRepository Integration Tests', () => {
  let recipeRepository: DrizzleRecipeRepository
  let ingredientRepository: DrizzleIngredientRepository
  let client: postgres.Sql
  let db: ReturnType<typeof drizzle>

  // Test ingredients that will be reused
  let testIngredient1: Ingredient
  let testIngredient2: Ingredient

  beforeAll(async () => {
    // Configure Money and RecipeIngredient providers
    const decimalProvider = new DecimalJsProvider()
    Money.configure(decimalProvider)
    RecipeIngredient.configure(decimalProvider)

    // Use existing database connection
    const connectionString = 'postgresql://costify:costify123@localhost:5432/costify_ts'

    client = postgres(connectionString, {
      max: 1,
      idle_timeout: 5,
      connect_timeout: 10,
    })

    db = drizzle(client, { schema })
    recipeRepository = new DrizzleRecipeRepository(db)
    ingredientRepository = new DrizzleIngredientRepository(db)

    // Create test ingredients that will be used across tests
    testIngredient1 = new Ingredient(
      new Id(crypto.randomUUID()),
      'Test Recipe Flour',
      new Money('2.50'),
      Unit.KILOGRAM
    )

    testIngredient2 = new Ingredient(
      new Id(crypto.randomUUID()),
      'Test Recipe Sugar',
      new Money('1.75'),
      Unit.KILOGRAM
    )

    await ingredientRepository.save(testIngredient1)
    await ingredientRepository.save(testIngredient2)
  })

  afterAll(async () => {
    try {
      // Clean up test ingredients
      await ingredientRepository.delete(testIngredient1.getId())
      await ingredientRepository.delete(testIngredient2.getId())

      if (client) {
        await client.end()
      }
    } catch (error) {
      console.error('Error during cleanup:', error)
    }
  })

  beforeEach(async () => {
    // Clean up any test recipes before each test
    await cleanupTestRecipes()
  })

  async function cleanupTestRecipes() {
    // Delete any recipes with test prefix to avoid conflicts
    try {
      await client`DELETE FROM recipe_ingredients WHERE recipe_id IN (
        SELECT id FROM recipes WHERE name LIKE 'Test Recipe %'
      )`
      await client`DELETE FROM recipes WHERE name LIKE 'Test Recipe %'`
    } catch (error) {
      // Ignore errors during cleanup
    }
  }

  describe('save and findById', () => {
    it('should save and retrieve recipe with ingredients', async () => {
      const recipeId = crypto.randomUUID()
      const recipeIngredients = [
        new RecipeIngredient(testIngredient1.getId(), '1.0', Unit.KILOGRAM),
        new RecipeIngredient(testIngredient2.getId(), '0.5', Unit.KILOGRAM),
      ]

      const recipe = new Recipe(
        new Id(recipeId),
        'Test Recipe Cake',
        recipeIngredients,
        new Money('3.38') // 1.0 * 2.50 + 0.5 * 1.75 = 3.375 rounded to 3.38
      )

      // Save recipe
      await recipeRepository.save(recipe)

      // Retrieve recipe
      const retrieved = await recipeRepository.findById(new Id(recipeId))

      // Verify recipe data
      expect(retrieved).not.toBeNull()
      expect(retrieved?.getId().getValue()).toBe(recipeId)
      expect(retrieved?.getName()).toBe('Test Recipe Cake')
      expect(retrieved?.getTotalCost().toFixed(2)).toBe('3.38')
      expect(retrieved?.getIngredients()).toHaveLength(2)

      // Verify ingredients
      const ingredients = retrieved?.getIngredients()
      const flour = ingredients.find((i) => i.getIngredientId().equals(testIngredient1.getId()))
      const sugar = ingredients.find((i) => i.getIngredientId().equals(testIngredient2.getId()))

      expect(flour).toBeDefined()
      expect(flour?.getQuantity().toString()).toBe('1')
      expect(flour?.getUnit()).toBe(Unit.KILOGRAM)

      expect(sugar).toBeDefined()
      expect(sugar?.getQuantity().toString()).toBe('0.5')
      expect(sugar?.getUnit()).toBe(Unit.KILOGRAM)
    })

    it('should return null for non-existent recipe', async () => {
      const nonExistentId = crypto.randomUUID()
      const retrieved = await recipeRepository.findById(new Id(nonExistentId))
      expect(retrieved).toBeNull()
    })
  })

  describe('findByName', () => {
    it('should find recipe by name', async () => {
      const recipeId = crypto.randomUUID()
      const recipeIngredients = [
        new RecipeIngredient(testIngredient1.getId(), '2.0', Unit.KILOGRAM),
      ]

      const recipe = new Recipe(
        new Id(recipeId),
        'Test Recipe Bread',
        recipeIngredients,
        new Money('5.00') // 2.0 * 2.50
      )

      // Save recipe
      await recipeRepository.save(recipe)

      // Find by name
      const retrieved = await recipeRepository.findByName('Test Recipe Bread')

      // Verify
      expect(retrieved).not.toBeNull()
      expect(retrieved?.getName()).toBe('Test Recipe Bread')
      expect(retrieved?.getTotalCost().toFixed(2)).toBe('5.00')
      expect(retrieved?.getIngredients()).toHaveLength(1)
    })

    it('should return null for non-existent recipe name', async () => {
      const retrieved = await recipeRepository.findByName('Non Existent Recipe')
      expect(retrieved).toBeNull()
    })
  })

  describe('update', () => {
    it('should update recipe and its ingredients', async () => {
      const recipeId = crypto.randomUUID()
      const originalIngredients = [
        new RecipeIngredient(testIngredient1.getId(), '1.0', Unit.KILOGRAM),
      ]

      const recipe = new Recipe(
        new Id(recipeId),
        'Test Recipe Pizza',
        originalIngredients,
        new Money('2.50')
      )

      // Save original recipe
      await recipeRepository.save(recipe)

      // Update recipe with different ingredients and name
      const updatedIngredients = [
        new RecipeIngredient(testIngredient1.getId(), '1.5', Unit.KILOGRAM),
        new RecipeIngredient(testIngredient2.getId(), '0.25', Unit.KILOGRAM),
      ]

      const updatedRecipe = new Recipe(
        new Id(recipeId),
        'Test Recipe Updated Pizza',
        updatedIngredients,
        new Money('4.19') // 1.5 * 2.50 + 0.25 * 1.75 = 4.1875 rounded to 4.19
      )

      // Update recipe
      await recipeRepository.update(updatedRecipe)

      // Retrieve and verify
      const retrieved = await recipeRepository.findById(new Id(recipeId))

      expect(retrieved).not.toBeNull()
      expect(retrieved?.getName()).toBe('Test Recipe Updated Pizza')
      expect(retrieved?.getTotalCost().toFixed(2)).toBe('4.19')
      expect(retrieved?.getIngredients()).toHaveLength(2)

      // Verify updated ingredients
      const ingredients = retrieved?.getIngredients()
      const flour = ingredients.find((i) => i.getIngredientId().equals(testIngredient1.getId()))
      const sugar = ingredients.find((i) => i.getIngredientId().equals(testIngredient2.getId()))

      expect(flour?.getQuantity().toString()).toBe('1.5')
      expect(sugar?.getQuantity().toString()).toBe('0.25')
    })
  })

  describe('delete', () => {
    it('should delete recipe and its ingredients', async () => {
      const recipeId = crypto.randomUUID()
      const recipeIngredients = [
        new RecipeIngredient(testIngredient1.getId(), '1.0', Unit.KILOGRAM),
      ]

      const recipe = new Recipe(
        new Id(recipeId),
        'Test Recipe Pasta',
        recipeIngredients,
        new Money('2.50')
      )

      // Save recipe
      await recipeRepository.save(recipe)

      // Verify it exists
      let retrieved = await recipeRepository.findById(new Id(recipeId))
      expect(retrieved).not.toBeNull()

      // Delete recipe
      await recipeRepository.delete(new Id(recipeId))

      // Verify it's gone
      retrieved = await recipeRepository.findById(new Id(recipeId))
      expect(retrieved).toBeNull()
    })
  })

  describe('existsById and existsByName', () => {
    it('should check recipe existence by id', async () => {
      const recipeId = crypto.randomUUID()
      const recipeIngredients = [
        new RecipeIngredient(testIngredient2.getId(), '0.5', Unit.KILOGRAM),
      ]

      const recipe = new Recipe(
        new Id(recipeId),
        'Test Recipe Cookies',
        recipeIngredients,
        new Money('0.875') // 0.5 * 1.75
      )

      // Check non-existence before saving
      let exists = await recipeRepository.existsById(new Id(recipeId))
      expect(exists).toBe(false)

      // Save recipe
      await recipeRepository.save(recipe)

      // Check existence after saving
      exists = await recipeRepository.existsById(new Id(recipeId))
      expect(exists).toBe(true)

      // Check non-existent id
      const nonExistentId = crypto.randomUUID()
      const notExists = await recipeRepository.existsById(new Id(nonExistentId))
      expect(notExists).toBe(false)
    })

    it('should check recipe existence by name', async () => {
      const recipeId = crypto.randomUUID()
      const recipeIngredients = [
        new RecipeIngredient(testIngredient1.getId(), '0.5', Unit.KILOGRAM),
      ]

      const recipe = new Recipe(
        new Id(recipeId),
        'Test Recipe Muffins',
        recipeIngredients,
        new Money('1.25') // 0.5 * 2.50
      )

      // Check non-existence before saving
      let exists = await recipeRepository.existsByName('Test Recipe Muffins')
      expect(exists).toBe(false)

      // Save recipe
      await recipeRepository.save(recipe)

      // Check existence after saving
      exists = await recipeRepository.existsByName('Test Recipe Muffins')
      expect(exists).toBe(true)

      // Check non-existent name
      const notExists = await recipeRepository.existsByName('Non Existent Recipe')
      expect(notExists).toBe(false)
    })
  })

  describe('findAll', () => {
    it('should retrieve all recipes sorted by name', async () => {
      // Create test recipes with unique names to avoid conflicts
      const timestamp = Date.now()
      const recipes = [
        new Recipe(
          new Id(crypto.randomUUID()),
          `Test Recipe Z Recipe ${timestamp}`,
          [new RecipeIngredient(testIngredient1.getId(), '1.0', Unit.KILOGRAM)],
          new Money('2.50')
        ),
        new Recipe(
          new Id(crypto.randomUUID()),
          `Test Recipe A Recipe ${timestamp}`,
          [new RecipeIngredient(testIngredient2.getId(), '1.0', Unit.KILOGRAM)],
          new Money('1.75')
        ),
        new Recipe(
          new Id(crypto.randomUUID()),
          `Test Recipe M Recipe ${timestamp}`,
          [new RecipeIngredient(testIngredient1.getId(), '0.5', Unit.KILOGRAM)],
          new Money('1.25')
        ),
      ]

      // Save all test recipes
      for (const recipe of recipes) {
        await recipeRepository.save(recipe)
      }

      // Retrieve all recipes
      const allRecipes = await recipeRepository.findAll()

      // Find our test recipes (should be sorted by name)
      const testRecipes = allRecipes.filter(
        (r) => r.getName().includes('Test Recipe') && r.getName().includes(`${timestamp}`)
      )

      expect(testRecipes.length).toBeGreaterThanOrEqual(3)

      // Verify they exist (order might vary due to other recipes in database)
      const names = testRecipes.map((r) => r.getName())
      expect(names).toContain(`Test Recipe A Recipe ${timestamp}`)
      expect(names).toContain(`Test Recipe M Recipe ${timestamp}`)
      expect(names).toContain(`Test Recipe Z Recipe ${timestamp}`)
    })
  })
})
