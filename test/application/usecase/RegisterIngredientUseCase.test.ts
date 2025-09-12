import { describe, expect, it, beforeEach, beforeAll, mock } from 'bun:test'
import { RegisterIngredientUseCase } from '@application/usecase/RegisterIngredientUseCase.js'
import type { IngredientRepository } from '@application/contracts/IngredientRepository.js'
import type { IdGenerator } from '@domain/contracts/IdGenerator.js'
import { Ingredient } from '@domain/entities/Ingredient.js'
import { Unit } from '@domain/valueobjects/Unit.js'
import { Money } from '@domain/valueobjects/Money.js'
import { Id } from '@domain/valueobjects/Id.js'
import { IngredientAlreadyExistsException } from '@application/errors/IngredientAlreadyExistsException.js'
import { DecimalJsProvider } from '@infrastructure/providers/DecimalJsProvider.js'

describe('RegisterIngredientUseCase', () => {
  let useCase: RegisterIngredientUseCase
  let mockRepository: IngredientRepository
  let mockIdGenerator: IdGenerator

  beforeAll(() => {
    Money.configure(new DecimalJsProvider())
  })

  beforeEach(() => {
    mockRepository = {
      save: mock(() => Promise.resolve()),
      findById: mock(() => Promise.resolve(null)),
      findByName: mock(() => Promise.resolve(null)),
      findAll: mock(() => Promise.resolve([])),
      update: mock(() => Promise.resolve()),
      delete: mock(() => Promise.resolve()),
      existsById: mock(() => Promise.resolve(false)),
      existsByName: mock(() => Promise.resolve(false)),
    }

    mockIdGenerator = {
      generate: mock(() => '550e8400-e29b-41d4-a716-446655440000')
    }

    useCase = new RegisterIngredientUseCase(mockRepository, mockIdGenerator)
  })

  it('should register new ingredient successfully', async () => {
    const command = {
      name: 'Flour',
      pricePerUnit: '2.50',
      unit: Unit.KILOGRAM
    }

    mockRepository.findByName = mock(() => Promise.resolve(null))

    const result = await useCase.execute(command)

    expect(result).toEqual({
      id: '550e8400-e29b-41d4-a716-446655440000',
      name: 'Flour',
      pricePerUnit: '2.50',
      unit: Unit.KILOGRAM
    })
    expect(mockRepository.save).toHaveBeenCalledTimes(1)
  })

  it('should throw error when ingredient already exists', async () => {
    const command = {
      name: 'Flour',
      pricePerUnit: '2.50',
      unit: Unit.KILOGRAM
    }

    const existingIngredient = new Ingredient(
      new Id('existing-id'),
      'Flour',
      new Money('2.50'),
      Unit.KILOGRAM
    )

    mockRepository.findByName = mock(() => Promise.resolve(existingIngredient))

    await expect(useCase.execute(command))
      .rejects.toThrow(IngredientAlreadyExistsException)

    expect(mockRepository.save).not.toHaveBeenCalled()
  })
})