import { Elysia, t } from 'elysia'
import { RegisterRecipeUseCase } from '@application/usecase/RegisterRecipeUseCase.js'
import { CalculateRecipeCostUseCase } from '@application/usecase/CalculateRecipeCostUseCase.js'
import { RecipeMapper } from '@application/mapper/RecipeMapper.js'
import type { RecipeRepository } from '@application/contracts/RecipeRepository.js'
import type { IngredientRepository } from '@application/contracts/IngredientRepository.js'
import type { IdGenerator } from '@domain/contracts/IdGenerator.js'
import { Id } from '@domain/valueobjects/Id.js'
import { Unit } from '@domain/valueobjects/Unit.js'

export class RecipeController {
  private readonly registerUseCase: RegisterRecipeUseCase
  private readonly calculateCostUseCase: CalculateRecipeCostUseCase

  constructor(
    private readonly recipeRepository: RecipeRepository,
    private readonly ingredientRepository: IngredientRepository,
    private readonly idGenerator: IdGenerator
  ) {
    this.registerUseCase = new RegisterRecipeUseCase(
      recipeRepository, 
      ingredientRepository, 
      idGenerator
    )
    this.calculateCostUseCase = new CalculateRecipeCostUseCase(
      recipeRepository, 
      ingredientRepository
    )
  }

  routes() {
    return new Elysia({ prefix: '/recipes' })
      .post('/', async ({ body }) => {
        const result = await this.registerUseCase.execute(body)
        return { success: true, data: result }
      }, {
        body: t.Object({
          name: t.String({ minLength: 1, maxLength: 255 }),
          ingredients: t.Array(t.Object({
            ingredientId: t.String(),
            quantity: t.Union([t.String(), t.Number()]),
            unit: t.Enum(Unit)
          }), { minItems: 1 })
        })
      })
      
      .get('/', async () => {
        const recipes = await this.recipeRepository.findAll()
        return { 
          success: true, 
          data: RecipeMapper.toDtoList(recipes)
        }
      })
      
      .get('/:id', async ({ params }) => {
        const recipe = await this.recipeRepository.findById(new Id(params.id))
        if (!recipe) {
          return { success: false, error: 'Recipe not found' }
        }
        return { 
          success: true, 
          data: RecipeMapper.toDto(recipe)
        }
      }, {
        params: t.Object({
          id: t.String()
        })
      })
      
      .get('/:id/cost', async ({ params }) => {
        const result = await this.calculateCostUseCase.execute({ 
          recipeId: params.id 
        })
        return { success: true, data: result }
      }, {
        params: t.Object({
          id: t.String()
        })
      })
      
      .delete('/:id', async ({ params }) => {
        const id = new Id(params.id)
        const exists = await this.recipeRepository.existsById(id)
        if (!exists) {
          return { success: false, error: 'Recipe not found' }
        }
        await this.recipeRepository.delete(id)
        return { success: true, message: 'Recipe deleted successfully' }
      }, {
        params: t.Object({
          id: t.String()
        })
      })
  }
}