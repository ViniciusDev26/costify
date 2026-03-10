import { useMutation, useQuery, useQueryClient } from "@tanstack/react-query";
import {
	getRecipeById,
	listRecipes,
	registerRecipe,
	updateRecipe,
} from "@/api/costify/queries/recipes";
import type {
	CreateRecipeInput,
	UpdateRecipeInput,
} from "@/api/costify/queries/recipes.types";

export function useRecipesList() {
	return useQuery({
		queryKey: ["recipes"],
		queryFn: () => listRecipes(),
	});
}

export function useRegisterRecipe() {
	const qc = useQueryClient();
	return useMutation({
		mutationFn: (input: CreateRecipeInput) => registerRecipe(input),
		onSuccess: () => {
			qc.invalidateQueries({ queryKey: ["recipes"] });
		},
	});
}

export function useRecipe(id: string) {
	return useQuery({
		queryKey: ["recipes", id],
		queryFn: () => getRecipeById(id),
		enabled: !!id,
	});
}

export function useUpdateRecipe(id: string) {
	const qc = useQueryClient();
	return useMutation({
		mutationFn: (input: UpdateRecipeInput) => updateRecipe(id, input),
		onSuccess: () => {
			qc.invalidateQueries({ queryKey: ["recipes"] });
			qc.invalidateQueries({ queryKey: ["recipes", id] });
		},
	});
}
