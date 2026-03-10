import { z } from "zod";
import { RecipeIngredientSchema } from "./create-recipe.schema";

export const UpdateRecipeSchema = z.object({
	name: z
		.string()
		.min(2, "Recipe name must be at least 2 characters")
		.max(100, "Recipe name must be at most 100 characters"),
	ingredients: z
		.array(RecipeIngredientSchema)
		.min(1, "Recipe must have at least one ingredient")
		.max(50, "Recipe cannot have more than 50 ingredients"),
});

export type UpdateRecipeInput = z.infer<typeof UpdateRecipeSchema>;
