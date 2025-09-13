import { Money } from '@domain/valueobjects/Money.js'
import { RecipeIngredient } from '@domain/valueobjects/RecipeIngredient.js'
import { IngredientController } from '../controllers/IngredientController.js'
import { RecipeController } from '../controllers/RecipeController.js'
import { client, db } from '../database/connection.js'
import { DecimalJsProvider } from '../providers/DecimalJsProvider.js'
import { UuidGenerator } from '../providers/UuidGenerator.js'
import { DrizzleIngredientRepository } from '../repositories/DrizzleIngredientRepository.js'
import { DrizzleRecipeRepository } from '../repositories/DrizzleRecipeRepository.js'

export class AppConfig {
  private static instance: AppConfig
  private readonly decimalProvider: DecimalJsProvider
  private readonly idGenerator: UuidGenerator
  private readonly ingredientRepository: DrizzleIngredientRepository
  private readonly recipeRepository: DrizzleRecipeRepository
  public readonly ingredientController: IngredientController
  public readonly recipeController: RecipeController

  private constructor() {
    // Initialize providers
    this.decimalProvider = new DecimalJsProvider()
    this.idGenerator = new UuidGenerator()

    // Configure domain value objects
    Money.configure(this.decimalProvider)
    RecipeIngredient.configure(this.decimalProvider)

    // Initialize repositories with Drizzle database
    this.ingredientRepository = new DrizzleIngredientRepository(db)
    this.recipeRepository = new DrizzleRecipeRepository(db)

    // Initialize controllers
    this.ingredientController = new IngredientController(
      this.ingredientRepository,
      this.idGenerator
    )
    this.recipeController = new RecipeController(
      this.recipeRepository,
      this.ingredientRepository,
      this.idGenerator
    )
  }

  static getInstance(): AppConfig {
    if (!AppConfig.instance) {
      AppConfig.instance = new AppConfig()
    }
    return AppConfig.instance
  }

  async close(): Promise<void> {
    await client.end()
  }

  getDatabase() {
    return db
  }
}
