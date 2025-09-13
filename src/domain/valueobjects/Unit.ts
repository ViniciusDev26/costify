import type { IDecimal } from '../contracts/DecimalProvider.js'

export enum Unit {
  GRAM = 'GRAM',
  KILOGRAM = 'KILOGRAM',
  MILLILITER = 'MILLILITER',
  LITER = 'LITER',
  PIECE = 'PIECE',
  UNIT = 'UNIT',
  TABLESPOON = 'TABLESPOON',
  TEASPOON = 'TEASPOON',
  CUP = 'CUP',
  OUNCE = 'OUNCE',
  POUND = 'POUND',
  TBSP = 'TBSP',
  TBSP_BUTTER = 'TBSP_BUTTER',
}

export interface ConversionRate {
  baseUnit: Unit
  factor: string
}

// Conversion rates to base units (in grams for weight, milliliters for volume, pieces for count)
const CONVERSION_RATES: Record<Unit, ConversionRate> = {
  // Weight conversions (base: gram)
  [Unit.GRAM]: { baseUnit: Unit.GRAM, factor: '1' },
  [Unit.KILOGRAM]: { baseUnit: Unit.GRAM, factor: '1000' },
  [Unit.OUNCE]: { baseUnit: Unit.GRAM, factor: '28.3495' },
  [Unit.POUND]: { baseUnit: Unit.GRAM, factor: '453.592' },

  // Volume conversions (base: milliliter)
  [Unit.MILLILITER]: { baseUnit: Unit.MILLILITER, factor: '1' },
  [Unit.LITER]: { baseUnit: Unit.MILLILITER, factor: '1000' },
  [Unit.TEASPOON]: { baseUnit: Unit.MILLILITER, factor: '4.92892' },
  [Unit.TABLESPOON]: { baseUnit: Unit.MILLILITER, factor: '14.7868' },
  [Unit.TBSP]: { baseUnit: Unit.MILLILITER, factor: '14.7868' }, // Same as tablespoon
  [Unit.TBSP_BUTTER]: { baseUnit: Unit.MILLILITER, factor: '14.7868' }, // Same as tablespoon for butter
  [Unit.CUP]: { baseUnit: Unit.MILLILITER, factor: '236.588' },

  // Count conversions (base: piece)
  [Unit.PIECE]: { baseUnit: Unit.PIECE, factor: '1' },
  [Unit.UNIT]: { baseUnit: Unit.PIECE, factor: '1' },
}

export function fromString(value: string): Unit {
  const upperValue = value.toUpperCase()
  if (Object.values(Unit).includes(upperValue as Unit)) {
    return upperValue as Unit
  }
  throw new Error(`Invalid unit: ${value}`)
}

export function isValid(value: string): boolean {
  try {
    fromString(value)
    return true
  } catch {
    return false
  }
}

export function getAllUnits(): Unit[] {
  return Object.values(Unit)
}

/**
 * Check if two units can be converted between each other
 */
export function canConvert(fromUnit: Unit, toUnit: Unit): boolean {
  const fromInfo = CONVERSION_RATES[fromUnit]
  const toInfo = CONVERSION_RATES[toUnit]
  return fromInfo.baseUnit === toInfo.baseUnit
}

/**
 * Convert quantity from one unit to another using precise decimal arithmetic
 * @param quantity - The quantity to convert (as IDecimal)
 * @param fromUnit - Source unit
 * @param toUnit - Target unit
 * @param decimalProvider - Factory to create decimal instances
 * @returns Converted quantity as IDecimal
 * @throws Error if units are incompatible
 */
export function convert(
  quantity: IDecimal,
  fromUnit: Unit,
  toUnit: Unit,
  decimalProvider: { create(value: string | number | IDecimal): IDecimal }
): IDecimal {
  if (!canConvert(fromUnit, toUnit)) {
    throw new Error(`Cannot convert from ${fromUnit} to ${toUnit}: incompatible unit types`)
  }

  if (fromUnit === toUnit) {
    return quantity
  }

  const fromInfo = CONVERSION_RATES[fromUnit]
  const toInfo = CONVERSION_RATES[toUnit]

  // Convert to base unit, then to target unit using precise decimal arithmetic
  const fromFactor = decimalProvider.create(fromInfo.factor)
  const toFactor = decimalProvider.create(toInfo.factor)
  
  const baseQuantity = quantity.multiply(fromFactor)
  return baseQuantity.divide(toFactor)
}

/**
 * Get the unit category (weight, volume, or count)
 */
export function getUnitCategory(unit: Unit): 'weight' | 'volume' | 'count' {
  const baseUnit = CONVERSION_RATES[unit].baseUnit
  switch (baseUnit) {
    case Unit.GRAM:
      return 'weight'
    case Unit.MILLILITER:
      return 'volume'
    case Unit.PIECE:
      return 'count'
    default:
      throw new Error(`Unknown base unit: ${baseUnit}`)
  }
}

/**
 * Get all units in the same category
 */
export function getUnitsInCategory(category: 'weight' | 'volume' | 'count'): Unit[] {
  return Object.values(Unit).filter(unit => getUnitCategory(unit) === category)
}

// Legacy compatibility - kept for backward compatibility but marked as deprecated
/** @deprecated Use named exports instead of UnitUtils class */
export const UnitUtils = {
  fromString,
  isValid,
  getAllUnits,
  canConvert,
  convert,
  getUnitCategory,
  getUnitsInCategory,
}
