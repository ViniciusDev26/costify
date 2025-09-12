import { ApplicationException } from './ApplicationException.js'

export class IngredientAlreadyExistsException extends ApplicationException {
  readonly errorCode = 'APPLICATION_INGREDIENT_001'
  
  constructor(name: string) {
    super(`Ingredient with name '${name}' already exists`)
  }
}