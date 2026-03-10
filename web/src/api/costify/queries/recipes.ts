import { client } from "../client";
import type {
	CreateRecipeInput,
	Recipe,
	RecipesListResponse,
	UpdateRecipeInput,
} from "./recipes.types";

export async function listRecipes() {
	const { data } = await client.get<RecipesListResponse>("/api/recipes");
	return data;
}

export async function registerRecipe(input: CreateRecipeInput) {
	const { data } = await client.post("/api/recipes", input);
	return data;
}

export async function getRecipeById(id: string) {
	const { data } = await client.get<Recipe>(`/api/recipes/${id}`);
	return data;
}

export async function updateRecipe(id: string, input: UpdateRecipeInput) {
	const { data } = await client.put(`/api/recipes/${id}`, input);
	return data;
}
