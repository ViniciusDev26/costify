import { DomainException } from '../DomainException.js'

export class InvalidQuantityException extends DomainException {
  readonly errorCode = 'DOMAIN_RECIPE_002'
  
  constructor(quantity: string) {
    super(`Invalid recipe ingredient quantity: ${quantity}`)
  }
}