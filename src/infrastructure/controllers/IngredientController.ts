import { Elysia, t } from 'elysia'
import { RegisterIngredientUseCase } from '@application/usecase/RegisterIngredientUseCase.js'
import { UpdateIngredientUseCase } from '@application/usecase/UpdateIngredientUseCase.js'
import { IngredientMapper } from '@application/mapper/IngredientMapper.js'
import type { IngredientRepository } from '@application/contracts/IngredientRepository.js'
import type { IdGenerator } from '@domain/contracts/IdGenerator.js'
import { Id } from '@domain/valueobjects/Id.js'
import { Unit } from '@domain/valueobjects/Unit.js'

export class IngredientController {
  private readonly registerUseCase: RegisterIngredientUseCase
  private readonly updateUseCase: UpdateIngredientUseCase

  constructor(
    private readonly ingredientRepository: IngredientRepository,
    private readonly idGenerator: IdGenerator
  ) {
    this.registerUseCase = new RegisterIngredientUseCase(ingredientRepository, idGenerator)
    this.updateUseCase = new UpdateIngredientUseCase(ingredientRepository, idGenerator)
  }

  routes() {
    return new Elysia({ prefix: '/ingredients' })
      .post('/', async ({ body }) => {
        const result = await this.registerUseCase.execute(body)
        return { success: true, data: result }
      }, {
        body: t.Object({
          name: t.String({ minLength: 1, maxLength: 255 }),
          pricePerUnit: t.Union([t.String(), t.Number()]),
          unit: t.Enum(Unit)
        })
      })
      
      .get('/', async () => {
        const ingredients = await this.ingredientRepository.findAll()
        return { 
          success: true, 
          data: IngredientMapper.toDtoList(ingredients)
        }
      })
      
      .get('/:id', async ({ params }) => {
        const ingredient = await this.ingredientRepository.findById(new Id(params.id))
        if (!ingredient) {
          return { success: false, error: 'Ingredient not found' }
        }
        return { 
          success: true, 
          data: IngredientMapper.toDto(ingredient)
        }
      }, {
        params: t.Object({
          id: t.String()
        })
      })
      
      .put('/:id', async ({ params, body }) => {
        const result = await this.updateUseCase.execute({
          id: params.id,
          ...body
        })
        return { success: true, data: result }
      }, {
        params: t.Object({
          id: t.String()
        }),
        body: t.Object({
          name: t.Optional(t.String({ minLength: 1, maxLength: 255 })),
          pricePerUnit: t.Optional(t.Union([t.String(), t.Number()])),
          unit: t.Optional(t.Enum(Unit))
        })
      })
      
      .delete('/:id', async ({ params }) => {
        const id = new Id(params.id)
        const exists = await this.ingredientRepository.existsById(id)
        if (!exists) {
          return { success: false, error: 'Ingredient not found' }
        }
        await this.ingredientRepository.delete(id)
        return { success: true, message: 'Ingredient deleted successfully' }
      }, {
        params: t.Object({
          id: t.String()
        })
      })
  }
}