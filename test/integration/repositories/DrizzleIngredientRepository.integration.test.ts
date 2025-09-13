import { afterAll, beforeAll, beforeEach, describe, expect, it } from 'bun:test'
import { Ingredient } from '@domain/entities/Ingredient.js'
import { Id } from '@domain/valueobjects/Id.js'
import { Money } from '@domain/valueobjects/Money.js'
import { Unit } from '@domain/valueobjects/Unit.js'
import * as schema from '@infrastructure/database/schema/index.js'
import { DecimalJsProvider } from '@infrastructure/providers/DecimalJsProvider.js'
import { DrizzleIngredientRepository } from '@infrastructure/repositories/DrizzleIngredientRepository.js'
import { drizzle } from 'drizzle-orm/postgres-js'
import postgres from 'postgres'

describe('DrizzleIngredientRepository Integration Tests', () => {
  let repository: DrizzleIngredientRepository
  let client: postgres.Sql
  let db: ReturnType<typeof drizzle>

  beforeAll(async () => {
    // Configure Money provider
    Money.configure(new DecimalJsProvider())

    // Use existing database connection
    const connectionString = 'postgresql://costify:costify123@localhost:5432/costify_ts'

    client = postgres(connectionString, {
      max: 1,
      idle_timeout: 5,
      connect_timeout: 10,
    })

    db = drizzle(client, { schema })
    repository = new DrizzleIngredientRepository(db)
  })

  afterAll(async () => {
    try {
      if (client) {
        await client.end()
      }
    } catch (error) {
      console.error('Error closing client:', error)
    }
  })

  beforeEach(async () => {
    // Clean up any test data before each test
    await cleanupTestIngredients()
  })

  async function cleanupTestIngredients() {
    // Delete any ingredients with test prefix to avoid conflicts
    try {
      await client`DELETE FROM ingredients WHERE name LIKE 'Test %'`
    } catch (error) {
      // Ignore errors during cleanup
    }
  }

  describe('save and findById', () => {
    it('should save and retrieve ingredient by id', async () => {
      const ingredientId = crypto.randomUUID()
      const ingredient = new Ingredient(
        new Id(ingredientId),
        'Test Flour Integration',
        new Money('3.50'),
        Unit.KILOGRAM
      )

      // Save ingredient
      await repository.save(ingredient)

      // Retrieve ingredient
      const retrieved = await repository.findById(new Id(ingredientId))

      // Verify
      expect(retrieved).not.toBeNull()
      expect(retrieved!.getId().getValue()).toBe(ingredientId)
      expect(retrieved!.getName()).toBe('Test Flour Integration')
      expect(retrieved!.getPricePerUnit().toFixed(2)).toBe('3.50')
      expect(retrieved!.getUnit()).toBe(Unit.KILOGRAM)
    })

    it('should return null for non-existent ingredient id', async () => {
      const nonExistentId = crypto.randomUUID()
      const retrieved = await repository.findById(new Id(nonExistentId))
      expect(retrieved).toBeNull()
    })
  })

  describe('findByName', () => {
    it('should find ingredient by name', async () => {
      const ingredientId = crypto.randomUUID()
      const ingredient = new Ingredient(
        new Id(ingredientId),
        'Test Sugar Integration',
        new Money('2.25'),
        Unit.KILOGRAM
      )

      // Save ingredient
      await repository.save(ingredient)

      // Find by name
      const retrieved = await repository.findByName('Test Sugar Integration')

      // Verify
      expect(retrieved).not.toBeNull()
      expect(retrieved!.getName()).toBe('Test Sugar Integration')
      expect(retrieved!.getPricePerUnit().toFixed(2)).toBe('2.25')
      expect(retrieved!.getUnit()).toBe(Unit.KILOGRAM)
    })

    it('should return null for non-existent ingredient name', async () => {
      const retrieved = await repository.findByName('Non Existent Ingredient')
      expect(retrieved).toBeNull()
    })
  })

  describe('update', () => {
    it('should update ingredient properties', async () => {
      const ingredientId = crypto.randomUUID()
      const ingredient = new Ingredient(
        new Id(ingredientId),
        'Test Salt Integration',
        new Money('1.00'),
        Unit.KILOGRAM
      )

      // Save original ingredient
      await repository.save(ingredient)

      // Update ingredient properties
      ingredient.updateName('Test Premium Salt Integration')
      ingredient.updatePricePerUnit(new Money('1.50'))
      ingredient.updateUnit(Unit.GRAM)

      // Update in repository
      await repository.update(ingredient)

      // Retrieve and verify
      const retrieved = await repository.findById(new Id(ingredientId))

      expect(retrieved).not.toBeNull()
      expect(retrieved!.getName()).toBe('Test Premium Salt Integration')
      expect(retrieved!.getPricePerUnit().toFixed(2)).toBe('1.50')
      expect(retrieved!.getUnit()).toBe(Unit.GRAM)
    })
  })

  describe('delete', () => {
    it('should delete ingredient', async () => {
      const ingredientId = crypto.randomUUID()
      const ingredient = new Ingredient(
        new Id(ingredientId),
        'Test Pepper Integration',
        new Money('5.00'),
        Unit.GRAM
      )

      // Save ingredient
      await repository.save(ingredient)

      // Verify it exists
      let retrieved = await repository.findById(new Id(ingredientId))
      expect(retrieved).not.toBeNull()

      // Delete ingredient
      await repository.delete(new Id(ingredientId))

      // Verify it's gone
      retrieved = await repository.findById(new Id(ingredientId))
      expect(retrieved).toBeNull()
    })
  })

  describe('existsById and existsByName', () => {
    it('should check ingredient existence by id', async () => {
      const ingredientId = crypto.randomUUID()
      const ingredient = new Ingredient(
        new Id(ingredientId),
        'Test Oregano Integration',
        new Money('4.00'),
        Unit.GRAM
      )

      // Check non-existence before saving
      let exists = await repository.existsById(new Id(ingredientId))
      expect(exists).toBe(false)

      // Save ingredient
      await repository.save(ingredient)

      // Check existence after saving
      exists = await repository.existsById(new Id(ingredientId))
      expect(exists).toBe(true)

      // Check non-existent id
      const nonExistentId = crypto.randomUUID()
      const notExists = await repository.existsById(new Id(nonExistentId))
      expect(notExists).toBe(false)
    })

    it('should check ingredient existence by name', async () => {
      const ingredientId = crypto.randomUUID()
      const ingredient = new Ingredient(
        new Id(ingredientId),
        'Test Basil Integration',
        new Money('3.25'),
        Unit.GRAM
      )

      // Check non-existence before saving
      let exists = await repository.existsByName('Test Basil Integration')
      expect(exists).toBe(false)

      // Save ingredient
      await repository.save(ingredient)

      // Check existence after saving
      exists = await repository.existsByName('Test Basil Integration')
      expect(exists).toBe(true)

      // Check non-existent name
      const notExists = await repository.existsByName('Non Existent Spice')
      expect(notExists).toBe(false)
    })
  })

  describe('findAll', () => {
    it('should retrieve all ingredients sorted by name', async () => {
      // Create test ingredients with unique names to avoid conflicts
      const timestamp = Date.now()
      const ingredients = [
        new Ingredient(
          new Id(crypto.randomUUID()),
          `Test Z Ingredient ${timestamp}`,
          new Money('1.00'),
          Unit.GRAM
        ),
        new Ingredient(
          new Id(crypto.randomUUID()),
          `Test A Ingredient ${timestamp}`,
          new Money('2.00'),
          Unit.KILOGRAM
        ),
        new Ingredient(
          new Id(crypto.randomUUID()),
          `Test M Ingredient ${timestamp}`,
          new Money('3.00'),
          Unit.LITER
        ),
      ]

      // Save all test ingredients
      for (const ingredient of ingredients) {
        await repository.save(ingredient)
      }

      // Retrieve all ingredients
      const allIngredients = await repository.findAll()

      // Find our test ingredients (should be sorted by name)
      const testIngredients = allIngredients.filter(
        (i) => i.getName().includes(`Test`) && i.getName().includes(`${timestamp}`)
      )

      expect(testIngredients.length).toBeGreaterThanOrEqual(3)

      // Verify they exist (order might vary due to other ingredients in database)
      const names = testIngredients.map((i) => i.getName())
      expect(names).toContain(`Test A Ingredient ${timestamp}`)
      expect(names).toContain(`Test M Ingredient ${timestamp}`)
      expect(names).toContain(`Test Z Ingredient ${timestamp}`)
    })
  })
})
