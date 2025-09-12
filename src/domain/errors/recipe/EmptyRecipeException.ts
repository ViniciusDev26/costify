import { DomainException } from '../DomainException.js'

export class EmptyRecipeException extends DomainException {
  readonly errorCode = 'DOMAIN_RECIPE_001'

  constructor() {
    super('Recipe must have at least one ingredient')
  }
}
