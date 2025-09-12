import { DomainException } from '../DomainException.js'

export class InvalidTotalCostException extends DomainException {
  readonly errorCode = 'DOMAIN_RECIPE_003'
  
  constructor(message: string) {
    super(`Invalid recipe total cost: ${message}`)
  }
}