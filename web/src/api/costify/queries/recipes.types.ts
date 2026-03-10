export type RecipeListItem = {
	id: string;
	name: string;
	totalCost?: number;
	createdAt?: string;
	updatedAt?: string;
};

export type RecipesListResponse = RecipeListItem[];

// ===== Cadastro =====
export type RecipeIngredientInput = {
	ingredientId: string;
	quantity: number;
	unit: string;
};

export type CreateRecipeInput = {
	name: string;
	ingredients: RecipeIngredientInput[];
};

// ===== Detalhes de uma receita =====
export type RecipeIngredientDetail = {
	ingredientId: string;
	ingredientName?: string;
	quantity: number;
	unit: string;
};

export type Recipe = {
	id: string;
	name: string;
	ingredients: RecipeIngredientDetail[];
	totalCost?: number;
	createdAt?: string;
	updatedAt?: string;
};

// ===== Atualização =====
export type UpdateRecipeInput = {
	name: string;
	ingredients: RecipeIngredientInput[];
};
