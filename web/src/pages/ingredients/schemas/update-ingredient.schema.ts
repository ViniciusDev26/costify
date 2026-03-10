import { z } from "zod";

export const UpdateIngredientSchema = z.object({
	name: z.string().min(1, "Name is required"),
	packageQuantity: z.coerce
		.number()
		.min(0.01, "Package quantity must be greater than 0"),
	packageUnit: z.string().min(1, "Package unit is required"),
	packagePrice: z.coerce.number().min(0.01, "Price must be greater than 0"),
});

export type UpdateIngredientInput = z.infer<typeof UpdateIngredientSchema>;
