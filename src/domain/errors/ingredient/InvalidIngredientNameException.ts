import { DomainException } from '../DomainException.js'

export class InvalidIngredientNameException extends DomainException {
  readonly errorCode = 'DOMAIN_INGREDIENT_001'
  
  constructor(name: string) {
    super(`Invalid ingredient name: ${name}`)
  }
}