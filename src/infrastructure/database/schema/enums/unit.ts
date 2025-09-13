import { pgEnum } from 'drizzle-orm/pg-core'

export const unitEnum = pgEnum('unit', [
  'GRAM',
  'KILOGRAM',
  'MILLILITER',
  'LITER',
  'UNIT',
  'TABLESPOON',
  'TEASPOON',
  'CUP',
  'OUNCE',
  'POUND',
  'TBSP',
  'TBSP_BUTTER',
])
