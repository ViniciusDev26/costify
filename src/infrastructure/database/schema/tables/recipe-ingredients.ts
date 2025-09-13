import { pgTable, uuid, decimal, timestamp } from 'drizzle-orm/pg-core'
import { relations } from 'drizzle-orm'
import { unitEnum } from '../enums/unit'
import { recipes } from './recipes'
import { ingredients } from './ingredients'

export const recipeIngredients = pgTable('recipe_ingredients', {
  id: uuid('id').primaryKey().defaultRandom(),
  recipeId: uuid('recipe_id')
    .references(() => recipes.id, { onDelete: 'cascade' })
    .notNull(),
  ingredientId: uuid('ingredient_id')
    .references(() => ingredients.id, { onDelete: 'cascade' })
    .notNull(),
  quantity: decimal('quantity', { precision: 10, scale: 4 }).notNull(),
  unit: unitEnum('unit').notNull(),
  createdAt: timestamp('created_at').defaultNow().notNull(),
})

export const recipeIngredientsRelations = relations(recipeIngredients, ({ one }) => ({
  recipe: one(recipes, {
    fields: [recipeIngredients.recipeId],
    references: [recipes.id],
  }),
  ingredient: one(ingredients, {
    fields: [recipeIngredients.ingredientId],
    references: [ingredients.id],
  }),
}))

export type InsertRecipeIngredient = typeof recipeIngredients.$inferInsert
export type SelectRecipeIngredient = typeof recipeIngredients.$inferSelect