import { Decimal } from 'decimal.js'
import type { IDecimal, DecimalProvider } from '@domain/contracts/DecimalProvider.js'

class DecimalJsWrapper implements IDecimal {
  constructor(private readonly decimal: Decimal) {}

  add(other: IDecimal): IDecimal {
    const otherDecimal = (other as DecimalJsWrapper).decimal
    return new DecimalJsWrapper(this.decimal.add(otherDecimal))
  }

  sub(other: IDecimal): IDecimal {
    const otherDecimal = (other as DecimalJsWrapper).decimal
    return new DecimalJsWrapper(this.decimal.sub(otherDecimal))
  }

  subtract(other: IDecimal): IDecimal {
    return this.sub(other)
  }

  multiply(other: IDecimal | string | number): IDecimal {
    if (typeof other === 'object' && 'toNumber' in other) {
      const otherDecimal = (other as DecimalJsWrapper).decimal
      return new DecimalJsWrapper(this.decimal.mul(otherDecimal))
    }
    return new DecimalJsWrapper(this.decimal.mul(other))
  }

  divide(other: IDecimal | string | number): IDecimal {
    if (typeof other === 'object' && 'toNumber' in other) {
      const otherDecimal = (other as DecimalJsWrapper).decimal
      return new DecimalJsWrapper(this.decimal.div(otherDecimal))
    }
    return new DecimalJsWrapper(this.decimal.div(other))
  }

  isZero(): boolean {
    return this.decimal.isZero()
  }

  isNegative(): boolean {
    return this.decimal.isNegative()
  }

  isPositive(): boolean {
    return this.decimal.isPositive()
  }

  isGreaterThan(other: IDecimal): boolean {
    const otherDecimal = (other as DecimalJsWrapper).decimal
    return this.decimal.greaterThan(otherDecimal)
  }

  greaterThan(other: IDecimal): boolean {
    return this.isGreaterThan(other)
  }

  isLessThan(other: IDecimal): boolean {
    const otherDecimal = (other as DecimalJsWrapper).decimal
    return this.decimal.lessThan(otherDecimal)
  }

  lessThan(other: IDecimal): boolean {
    return this.isLessThan(other)
  }

  equals(other: IDecimal): boolean {
    const otherDecimal = (other as DecimalJsWrapper).decimal
    return this.decimal.equals(otherDecimal)
  }

  toNumber(): number {
    return this.decimal.toNumber()
  }

  toFixed(decimalPlaces = 2): string {
    return this.decimal.toFixed(decimalPlaces)
  }

  toString(): string {
    return this.decimal.toString()
  }
}

export class DecimalJsProvider implements DecimalProvider {
  create(value: string | number | IDecimal): IDecimal {
    if (typeof value === 'object' && 'toNumber' in value) {
      return value
    }
    return new DecimalJsWrapper(new Decimal(value))
  }

  zero(): IDecimal {
    return new DecimalJsWrapper(new Decimal(0))
  }
}
