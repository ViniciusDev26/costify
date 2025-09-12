import { describe, expect, it } from 'vitest'
import { Ingredient } from '@domain/entities/Ingredient.js'
import { Id } from '@domain/valueobjects/Id.js'
import { Money } from '@domain/valueobjects/Money.js'
import { Unit } from '@domain/valueobjects/Unit.js'

describe('Ingredient', () => {
  const validId = new Id('550e8400-e29b-41d4-a716-446655440000')
  const validPrice = new Money('2.50')

  describe('creation', () => {
    it('should create ingredient with valid data', () => {
      const ingredient = new Ingredient(validId, 'Flour', validPrice, Unit.KILOGRAM)
      
      expect(ingredient.getId()).toEqual(validId)
      expect(ingredient.getName()).toBe('Flour')
      expect(ingredient.getPricePerUnit()).toEqual(validPrice)
      expect(ingredient.getUnit()).toBe(Unit.KILOGRAM)
    })

    it('should throw error for empty name', () => {
      expect(() => new Ingredient(validId, '', validPrice, Unit.KILOGRAM))
        .toThrow('Invalid ingredient name')
    })

    it('should throw error for null name', () => {
      expect(() => new Ingredient(validId, null as any, validPrice, Unit.KILOGRAM))
        .toThrow('Invalid ingredient name')
    })

    it('should trim ingredient name', () => {
      const ingredient = new Ingredient(validId, '  Flour  ', validPrice, Unit.KILOGRAM)
      expect(ingredient.getName()).toBe('Flour')
    })
  })

  describe('updates', () => {
    it('should update ingredient name', () => {
      const ingredient = new Ingredient(validId, 'Flour', validPrice, Unit.KILOGRAM)
      ingredient.updateName('Bread Flour')
      expect(ingredient.getName()).toBe('Bread Flour')
    })

    it('should update price per unit', () => {
      const ingredient = new Ingredient(validId, 'Flour', validPrice, Unit.KILOGRAM)
      const newPrice = new Money('3.00')
      ingredient.updatePricePerUnit(newPrice)
      expect(ingredient.getPricePerUnit()).toEqual(newPrice)
    })

    it('should update unit', () => {
      const ingredient = new Ingredient(validId, 'Flour', validPrice, Unit.KILOGRAM)
      ingredient.updateUnit(Unit.GRAM)
      expect(ingredient.getUnit()).toBe(Unit.GRAM)
    })
  })

  describe('cost calculation', () => {
    it('should calculate cost for given quantity', () => {
      const ingredient = new Ingredient(validId, 'Flour', new Money('2.50'), Unit.KILOGRAM)
      const cost = ingredient.calculateCost('2')
      expect(cost.toFixed(2)).toBe('5.00')
    })
  })

  describe('equality', () => {
    it('should be equal if same id', () => {
      const ingredient1 = new Ingredient(validId, 'Flour', validPrice, Unit.KILOGRAM)
      const ingredient2 = new Ingredient(validId, 'Different Name', new Money('5.00'), Unit.GRAM)
      
      expect(ingredient1.equals(ingredient2)).toBe(true)
    })

    it('should not be equal if different id', () => {
      const ingredient1 = new Ingredient(validId, 'Flour', validPrice, Unit.KILOGRAM)
      const ingredient2 = new Ingredient(new Id('different-id'), 'Flour', validPrice, Unit.KILOGRAM)
      
      expect(ingredient1.equals(ingredient2)).toBe(false)
    })
  })
})