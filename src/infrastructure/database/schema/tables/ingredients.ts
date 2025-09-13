import { relations } from 'drizzle-orm'
import { decimal, pgTable, timestamp, uuid, varchar } from 'drizzle-orm/pg-core'
import { unitEnum } from '../enums/unit'
import { recipeIngredients } from './recipe-ingredients'

export const ingredients = pgTable('ingredients', {
  id: uuid('id').primaryKey().defaultRandom(),
  name: varchar('name', { length: 255 }).notNull().unique(),
  pricePerUnit: decimal('price_per_unit', { precision: 10, scale: 2 }).notNull(),
  unit: unitEnum('unit').notNull(),
  createdAt: timestamp('created_at').defaultNow().notNull(),
  updatedAt: timestamp('updated_at').defaultNow().notNull(),
})

export const ingredientsRelations = relations(ingredients, ({ many }) => ({
  recipeIngredients: many(recipeIngredients),
}))

export type InsertIngredient = typeof ingredients.$inferInsert
export type SelectIngredient = typeof ingredients.$inferSelect
