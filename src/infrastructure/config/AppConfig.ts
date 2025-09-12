import { PrismaClient } from '@prisma/client'
import { Money } from '@domain/valueobjects/Money.js'
import { RecipeIngredient } from '@domain/valueobjects/RecipeIngredient.js'
import { DecimalJsProvider } from '../providers/DecimalJsProvider.js'
import { UuidGenerator } from '../providers/UuidGenerator.js'
import { PrismaIngredientRepository } from '../repositories/PrismaIngredientRepository.js'
import { PrismaRecipeRepository } from '../repositories/PrismaRecipeRepository.js'
import { IngredientController } from '../controllers/IngredientController.js'
import { RecipeController } from '../controllers/RecipeController.js'

export class AppConfig {
  private static instance: AppConfig
  private readonly prisma: PrismaClient
  private readonly decimalProvider: DecimalJsProvider
  private readonly idGenerator: UuidGenerator
  private readonly ingredientRepository: PrismaIngredientRepository
  private readonly recipeRepository: PrismaRecipeRepository
  public readonly ingredientController: IngredientController
  public readonly recipeController: RecipeController

  private constructor() {
    // Initialize providers
    this.decimalProvider = new DecimalJsProvider()
    this.idGenerator = new UuidGenerator()
    
    // Configure domain value objects
    Money.configure(this.decimalProvider)
    RecipeIngredient.configure(this.decimalProvider)

    // Initialize database
    this.prisma = new PrismaClient({
      log: process.env.NODE_ENV === 'development' ? ['query', 'error', 'warn'] : ['error']
    })

    // Initialize repositories
    this.ingredientRepository = new PrismaIngredientRepository(this.prisma)
    this.recipeRepository = new PrismaRecipeRepository(this.prisma)

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
    await this.prisma.$disconnect()
  }

  getPrisma(): PrismaClient {
    return this.prisma
  }
}