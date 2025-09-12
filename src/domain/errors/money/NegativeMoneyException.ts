import { DomainException } from '../DomainException.js'

export class NegativeMoneyException extends DomainException {
  readonly errorCode = 'DOMAIN_MONEY_001'
  
  constructor(amount: string) {
    super(`Money amount cannot be negative: ${amount}`)
  }
}