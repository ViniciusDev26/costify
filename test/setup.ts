import { beforeAll } from 'vitest'
import { Money } from '@domain/valueobjects/Money.js'
import { RecipeIngredient } from '@domain/valueobjects/RecipeIngredient.js'
import { DecimalJsProvider } from '@infrastructure/providers/DecimalJsProvider.js'

beforeAll(() => {
  // Configure domain value objects for testing
  const decimalProvider = new DecimalJsProvider()
  Money.configure(decimalProvider)
  RecipeIngredient.configure(decimalProvider)
})