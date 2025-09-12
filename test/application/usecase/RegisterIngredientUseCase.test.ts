import { describe, expect, it, beforeEach, vi } from 'vitest'
import { RegisterIngredientUseCase } from '@application/usecase/RegisterIngredientUseCase.js'
import type { IngredientRepository } from '@application/contracts/IngredientRepository.js'
import type { IdGenerator } from '@domain/contracts/IdGenerator.js'
import { Ingredient } from '@domain/entities/Ingredient.js'
import { Unit } from '@domain/valueobjects/Unit.js'
import { IngredientAlreadyExistsException } from '@application/errors/IngredientAlreadyExistsException.js'

describe('RegisterIngredientUseCase', () => {
  let useCase: RegisterIngredientUseCase
  let mockRepository: IngredientRepository
  let mockIdGenerator: IdGenerator

  beforeEach(() => {
    mockRepository = {
      save: vi.fn(),
      findById: vi.fn(),
      findByName: vi.fn(),
      findAll: vi.fn(),
      update: vi.fn(),
      delete: vi.fn(),
      existsById: vi.fn(),
      existsByName: vi.fn(),
    }

    mockIdGenerator = {
      generate: vi.fn(() => '550e8400-e29b-41d4-a716-446655440000')
    }

    useCase = new RegisterIngredientUseCase(mockRepository, mockIdGenerator)
  })

  it('should register new ingredient successfully', async () => {
    const command = {
      name: 'Flour',
      pricePerUnit: '2.50',
      unit: Unit.KILOGRAM
    }

    vi.mocked(mockRepository.findByName).mockResolvedValue(null)

    const result = await useCase.execute(command)

    expect(result).toEqual({
      id: '550e8400-e29b-41d4-a716-446655440000',
      name: 'Flour',
      pricePerUnit: '2.50',
      unit: Unit.KILOGRAM
    })
    expect(mockRepository.save).toHaveBeenCalledOnce()
  })

  it('should throw error when ingredient already exists', async () => {
    const command = {
      name: 'Flour',
      pricePerUnit: '2.50',
      unit: Unit.KILOGRAM
    }

    const existingIngredient = new Ingredient(
      { getValue: () => 'existing-id' } as any,
      'Flour',
      { toFixed: () => '2.50' } as any,
      Unit.KILOGRAM
    )

    vi.mocked(mockRepository.findByName).mockResolvedValue(existingIngredient)

    await expect(useCase.execute(command))
      .rejects.toThrow(IngredientAlreadyExistsException)

    expect(mockRepository.save).not.toHaveBeenCalled()
  })
})