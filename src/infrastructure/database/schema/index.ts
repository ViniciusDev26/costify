import { pgTable, uuid, varchar, decimal, timestamp, pgEnum } from 'drizzle-orm/pg-core'
import { relations } from 'drizzle-orm'

// Define the Unit enum
export const unitEnum = pgEnum('unit', [
  'GRAM',
  'KILOGRAM',
  'MILLILITER',
  'LITER',
  'PIECE',
  'TABLESPOON',
  'TEASPOON',
  'CUP',
  'OUNCE',
  'POUND',
  'TBSP',
  'TBSP_BUTTER',
])

// Ingredients table
export const ingredients = pgTable('ingredients', {
  id: uuid('id').primaryKey().defaultRandom(),
  name: varchar('name', { length: 255 }).notNull().unique(),
  pricePerUnit: decimal('price_per_unit', { precision: 10, scale: 2 }).notNull(),
  unit: unitEnum('unit').notNull(),
  createdAt: timestamp('created_at').defaultNow().notNull(),
  updatedAt: timestamp('updated_at').defaultNow().notNull(),
})

// Recipes table
export const recipes = pgTable('recipes', {
  id: uuid('id').primaryKey().defaultRandom(),
  name: varchar('name', { length: 255 }).notNull().unique(),
  totalCost: decimal('total_cost', { precision: 10, scale: 2 }).notNull(),
  createdAt: timestamp('created_at').defaultNow().notNull(),
  updatedAt: timestamp('updated_at').defaultNow().notNull(),
})

// Recipe ingredients junction table
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

// Define relations
export const ingredientsRelations = relations(ingredients, ({ many }) => ({
  recipeIngredients: many(recipeIngredients),
}))

export const recipesRelations = relations(recipes, ({ many }) => ({
  recipeIngredients: many(recipeIngredients),
}))

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

// Type exports for use in application
export type InsertIngredient = typeof ingredients.$inferInsert
export type SelectIngredient = typeof ingredients.$inferSelect

export type InsertRecipe = typeof recipes.$inferInsert
export type SelectRecipe = typeof recipes.$inferSelect

export type InsertRecipeIngredient = typeof recipeIngredients.$inferInsert
export type SelectRecipeIngredient = typeof recipeIngredients.$inferSelect
