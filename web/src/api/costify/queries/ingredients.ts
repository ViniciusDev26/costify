import { client } from "../client";
import type {
	CreateIngredientInput,
	Ingredient,
	IngredientsListResponse,
	UpdateIngredientInput,
} from "./ingredients.types";

export async function registerIngredient(input: CreateIngredientInput) {
	const { data } = await client.post("/api/ingredients", input);
	return data;
}

export async function listIngredients() {
	const { data } =
		await client.get<IngredientsListResponse>("/api/ingredients");
	return data;
}

export async function getIngredientById(id: string) {
	const { data } = await client.get<Ingredient>(`/api/ingredients/${id}`);
	return data;
}

export async function updateIngredient(
	id: string,
	input: UpdateIngredientInput,
) {
	const { data } = await client.put(`/api/ingredients/${id}`, input);
	return data;
}
