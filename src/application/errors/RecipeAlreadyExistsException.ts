import { ApplicationException } from './ApplicationException.js'

export class RecipeAlreadyExistsException extends ApplicationException {
  readonly errorCode = 'APPLICATION_RECIPE_001'
  
  constructor(name: string) {
    super(`Recipe with name '${name}' already exists`)
  }
}