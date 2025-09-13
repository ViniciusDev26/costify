import type { DecimalProvider, IDecimal } from '../contracts/DecimalProvider.js'

export class Money {
  private readonly amount: IDecimal
  private static decimalProvider: DecimalProvider

  constructor(amount: string | number | IDecimal) {
    if (!Money.decimalProvider) {
      throw new Error('DecimalProvider must be configured before using Money')
    }

    const decimal =
      typeof amount === 'object' && 'toNumber' in amount
        ? amount
        : Money.decimalProvider.create(amount)

    if (decimal.isNegative()) {
      throw new Error('Money amount cannot be negative')
    }
    this.amount = decimal
  }

  static configure(decimalProvider: DecimalProvider): void {
    Money.decimalProvider = decimalProvider
  }

  static zero(): Money {
    if (!Money.decimalProvider) {
      throw new Error('DecimalProvider must be configured before using Money')
    }
    return new Money(Money.decimalProvider.zero())
  }

  add(other: Money): Money {
    return new Money(this.amount.add(other.amount))
  }

  subtract(other: Money): Money {
    const result = this.amount.sub(other.amount)
    if (result.isNegative()) {
      throw new Error('Subtraction would result in negative money')
    }
    return new Money(result)
  }

  multiply(multiplier: string | number | IDecimal): Money {
    const multiplierDecimal =
      typeof multiplier === 'object' && 'toNumber' in multiplier
        ? multiplier
        : Money.decimalProvider.create(multiplier)
    return new Money(this.amount.multiply(multiplierDecimal))
  }

  divide(divisor: string | number | IDecimal): Money {
    const divisorDecimal =
      typeof divisor === 'object' && 'toNumber' in divisor
        ? divisor
        : Money.decimalProvider.create(divisor)
    if (divisorDecimal.isZero()) {
      throw new Error('Cannot divide by zero')
    }
    return new Money(this.amount.divide(divisorDecimal))
  }

  isZero(): boolean {
    return this.amount.isZero()
  }

  isGreaterThan(other: Money): boolean {
    return this.amount.greaterThan(other.amount)
  }

  isLessThan(other: Money): boolean {
    return this.amount.lessThan(other.amount)
  }

  equals(other: Money): boolean {
    if (!(other instanceof Money)) {
      return false
    }
    return this.amount.equals(other.amount)
  }

  toNumber(): number {
    return this.amount.toNumber()
  }

  toFixed(decimalPlaces = 2): string {
    return this.amount.toFixed(decimalPlaces)
  }

  toString(): string {
    return this.amount.toString()
  }
}
