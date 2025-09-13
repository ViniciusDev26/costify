import { pgTable, uuid, varchar, decimal, timestamp } from 'drizzle-orm/pg-core'
import { relations } from 'drizzle-orm'
import { recipeIngredients } from './recipe-ingredients'

export const recipes = pgTable('recipes', {
  id: uuid('id').primaryKey().defaultRandom(),
  name: varchar('name', { length: 255 }).notNull().unique(),
  totalCost: decimal('total_cost', { precision: 10, scale: 2 }).notNull(),
  createdAt: timestamp('created_at').defaultNow().notNull(),
  updatedAt: timestamp('updated_at').defaultNow().notNull(),
})

export const recipesRelations = relations(recipes, ({ many }) => ({
  recipeIngredients: many(recipeIngredients),
}))

export type InsertRecipe = typeof recipes.$inferInsert
export type SelectRecipe = typeof recipes.$inferSelect