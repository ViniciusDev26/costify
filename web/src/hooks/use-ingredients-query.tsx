import { useMutation, useQuery, useQueryClient } from "@tanstack/react-query";
import {
	getIngredientById,
	listIngredients,
	registerIngredient,
	updateIngredient,
} from "@/api/costify/queries/ingredients";
import type {
	CreateIngredientInput,
	UpdateIngredientInput,
} from "@/api/costify/queries/ingredients.types";

export function useRegisterIngredient() {
	const qc = useQueryClient();
	return useMutation({
		mutationFn: (input: CreateIngredientInput) => registerIngredient(input),
		onSuccess: () => {
			qc.invalidateQueries({ queryKey: ["ingredients"] });
		},
	});
}

export function useIngredientsList() {
	return useQuery({
		queryKey: ["ingredients"],
		queryFn: () => listIngredients(),
	});
}

export function useIngredient(id: string) {
	return useQuery({
		queryKey: ["ingredients", id],
		queryFn: () => getIngredientById(id),
		enabled: !!id,
	});
}

export function useUpdateIngredient(id: string) {
	const qc = useQueryClient();
	return useMutation({
		mutationFn: (input: UpdateIngredientInput) => updateIngredient(id, input),
		onSuccess: () => {
			qc.invalidateQueries({ queryKey: ["ingredients"] });
			qc.invalidateQueries({ queryKey: ["ingredients", id] });
		},
	});
}
