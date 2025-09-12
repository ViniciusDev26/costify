export interface IDecimal {
  add(other: IDecimal): IDecimal
  sub(other: IDecimal): IDecimal
  subtract(other: IDecimal): IDecimal
  multiply(other: IDecimal | string | number): IDecimal
  divide(other: IDecimal | string | number): IDecimal
  isZero(): boolean
  isNegative(): boolean
  isPositive(): boolean
  isGreaterThan(other: IDecimal): boolean
  greaterThan(other: IDecimal): boolean
  isLessThan(other: IDecimal): boolean
  lessThan(other: IDecimal): boolean
  equals(other: IDecimal): boolean
  toNumber(): number
  toFixed(decimalPlaces?: number): string
  toString(): string
}

export interface DecimalProvider {
  create(value: string | number | IDecimal): IDecimal
  zero(): IDecimal
}
