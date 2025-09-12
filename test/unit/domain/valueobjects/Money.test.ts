import { describe, expect, it, beforeAll } from 'bun:test'
import { Money } from '@domain/valueobjects/Money.js'
import { DecimalJsProvider } from '@infrastructure/providers/DecimalJsProvider.js'

describe('Money', () => {
  beforeAll(() => {
    Money.configure(new DecimalJsProvider())
  })
  describe('creation', () => {
    it('should create money with valid amount', () => {
      const money = new Money('10.50')
      expect(money.toFixed(2)).toBe('10.50')
    })

    it('should create zero money', () => {
      const money = Money.zero()
      expect(money.isZero()).toBe(true)
    })

    it('should throw error for negative amount', () => {
      expect(() => new Money('-5.00')).toThrow('Money amount cannot be negative')
    })
  })

  describe('operations', () => {
    it('should add two money values', () => {
      const money1 = new Money('10.50')
      const money2 = new Money('5.25')
      const result = money1.add(money2)
      expect(result.toFixed(2)).toBe('15.75')
    })

    it('should subtract money values', () => {
      const money1 = new Money('10.50')
      const money2 = new Money('5.25')
      const result = money1.subtract(money2)
      expect(result.toFixed(2)).toBe('5.25')
    })

    it('should multiply money by scalar', () => {
      const money = new Money('10.50')
      const result = money.multiply('2')
      expect(result.toFixed(2)).toBe('21.00')
    })

    it('should divide money by scalar', () => {
      const money = new Money('21.00')
      const result = money.divide('2')
      expect(result.toFixed(2)).toBe('10.50')
    })

    it('should throw error when subtracting larger amount', () => {
      const money1 = new Money('5.00')
      const money2 = new Money('10.00')
      expect(() => money1.subtract(money2)).toThrow('Subtraction would result in negative money')
    })

    it('should throw error when dividing by zero', () => {
      const money = new Money('10.00')
      expect(() => money.divide('0')).toThrow('Cannot divide by zero')
    })
  })

  describe('comparisons', () => {
    it('should compare money values correctly', () => {
      const money1 = new Money('10.50')
      const money2 = new Money('5.25')
      const money3 = new Money('10.50')

      expect(money1.isGreaterThan(money2)).toBe(true)
      expect(money2.isLessThan(money1)).toBe(true)
      expect(money1.equals(money3)).toBe(true)
    })
  })
})