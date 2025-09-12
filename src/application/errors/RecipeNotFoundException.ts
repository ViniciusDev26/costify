import { ApplicationException } from './ApplicationException.js'

export class RecipeNotFoundException extends ApplicationException {
  readonly errorCode = 'APPLICATION_RECIPE_002'

  constructor(identifier: string) {
    super(`Recipe not found: ${identifier}`)
  }
}
