import { z } from "zod";

const uuidRegex =
	/^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}$/;

export const RecipeIngredientSchema = z.object({
	ingredientId: z
		.string()
		.min(1, "Ingredient is required")
		.regex(uuidRegex, "Invalid ingredient ID"),
	quantity: z.number().min(0.01, "Quantity must be greater than 0"),
	unit: z.string().min(1, "Unit is required"),
});

export const CreateRecipeSchema = z.object({
	name: z
		.string()
		.min(2, "Recipe name must be at least 2 characters")
		.max(100, "Recipe name must be at most 100 characters"),
	ingredients: z
		.array(RecipeIngredientSchema)
		.min(1, "Recipe must have at least one ingredient")
		.max(50, "Recipe cannot have more than 50 ingredients"),
});

export type RecipeIngredientInput = z.infer<typeof RecipeIngredientSchema>;
export type CreateRecipeInput = z.infer<typeof CreateRecipeSchema>;
