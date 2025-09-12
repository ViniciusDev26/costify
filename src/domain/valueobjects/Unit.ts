export enum Unit {
  GRAM = 'GRAM',
  KILOGRAM = 'KILOGRAM',
  MILLILITER = 'MILLILITER',
  LITER = 'LITER',
  PIECE = 'PIECE',
  TABLESPOON = 'TABLESPOON',
  TEASPOON = 'TEASPOON',
  CUP = 'CUP',
  OUNCE = 'OUNCE',
  POUND = 'POUND',
  TBSP = 'TBSP',
  TBSP_BUTTER = 'TBSP_BUTTER',
}

export class UnitUtils {
  static fromString(value: string): Unit {
    const upperValue = value.toUpperCase()
    if (Object.values(Unit).includes(upperValue as Unit)) {
      return upperValue as Unit
    }
    throw new Error(`Invalid unit: ${value}`)
  }

  static isValid(value: string): boolean {
    try {
      UnitUtils.fromString(value)
      return true
    } catch {
      return false
    }
  }

  static getAllUnits(): Unit[] {
    return Object.values(Unit)
  }
}