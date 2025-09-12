import { ApplicationException } from './ApplicationException.js'

export class IngredientNotFoundException extends ApplicationException {
  readonly errorCode = 'APPLICATION_INGREDIENT_002'

  constructor(identifier: string) {
    super(`Ingredient not found: ${identifier}`)
  }
}
