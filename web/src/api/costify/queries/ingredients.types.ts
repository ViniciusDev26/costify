export type CreateIngredientInput = {
	name: string;
	packagePrice: number;
	packageQuantity: number;
	packageUnit: string;
};

export type UpdateIngredientInput = {
	name: string;
	packagePrice: number;
	packageQuantity: number;
	packageUnit: string;
};

// ===== Listagem =====
export type Ingredient = {
	id: string;
	name: string;
	packageQuantity: number;
	packagePrice: number;
	packageUnit: string;
	unitCost?: number; // pode vir calculado pelo backend
};

export type IngredientsListResponse = Ingredient[];
