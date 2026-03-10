import { zodResolver } from "@hookform/resolvers/zod";
import { Plus, Trash2 } from "lucide-react";
import { useEffect } from "react";
import { Controller, useFieldArray, useForm } from "react-hook-form";
import type { Recipe } from "@/api/costify/queries/recipes.types";
import { Alert, AlertDescription } from "@/components/ui/alert";
import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import { InputGroup } from "@/components/ui/input-group";
import { Label } from "@/components/ui/label";
import {
	Select,
	SelectContent,
	SelectItem,
	SelectTrigger,
	SelectValue,
} from "@/components/ui/select";
import { useIngredientsList } from "@/hooks/use-ingredients-query";
import { useRegisterRecipe, useUpdateRecipe } from "@/hooks/use-recipes-query";
import { useUnits } from "@/hooks/use-units-query";
import {
	type CreateRecipeInput,
	CreateRecipeSchema,
} from "../schemas/create-recipe.schema";
import {
	type UpdateRecipeInput,
	UpdateRecipeSchema,
} from "../schemas/update-recipe.schema";

interface RecipeFormProps {
	onSuccess?: () => void;
	mode?: "create" | "edit";
	recipeId?: string;
	initialData?: Recipe;
}

export function RecipeForm({
	onSuccess,
	mode = "create",
	recipeId,
	initialData,
}: RecipeFormProps) {
	const { data: ingredients } = useIngredientsList();
	const { data: units } = useUnits();
	const {
		mutateAsync: registerRecipe,
		isPending: isCreating,
		error: createError,
	} = useRegisterRecipe();
	const {
		mutateAsync: updateRecipe,
		isPending: isUpdating,
		error: updateError,
	} = useUpdateRecipe(recipeId || "");

	const {
		register,
		handleSubmit,
		control,
		reset,
		formState: { errors },
	} = useForm<CreateRecipeInput | UpdateRecipeInput>({
		resolver: zodResolver(
			mode === "create" ? CreateRecipeSchema : UpdateRecipeSchema,
		),
		defaultValues: initialData
			? {
					name: initialData.name,
					ingredients: initialData.ingredients.map((ing) => ({
						ingredientId: ing.ingredientId,
						quantity: ing.quantity,
						unit: ing.unit,
					})),
				}
			: {
					name: "",
					ingredients: [{ ingredientId: "", quantity: 0, unit: "" }],
				},
	});

	useEffect(() => {
		if (initialData) {
			reset({
				name: initialData.name,
				ingredients: initialData.ingredients.map((ing) => ({
					ingredientId: ing.ingredientId,
					quantity: ing.quantity,
					unit: ing.unit,
				})),
			});
		}
	}, [initialData, reset]);

	const { fields, append, remove } = useFieldArray({
		control,
		name: "ingredients",
	});

	async function onSubmit(data: CreateRecipeInput | UpdateRecipeInput) {
		if (mode === "edit") {
			await updateRecipe(data);
		} else {
			await registerRecipe(data);
			reset();
		}
		onSuccess?.();
	}

	const isPending = isCreating || isUpdating;
	const error = createError || updateError;

	return (
		<form className="flex flex-col gap-4" onSubmit={handleSubmit(onSubmit)}>
			<InputGroup>
				<Label htmlFor="name">Nome da receita</Label>
				<Input id="name" {...register("name")} />
				{errors.name && (
					<p className="text-sm text-destructive">{errors.name.message}</p>
				)}
			</InputGroup>

			<div className="space-y-4">
				<div className="flex items-center justify-between">
					<Label>Ingredientes</Label>
					<Button
						type="button"
						variant="outline"
						size="sm"
						onClick={() => append({ ingredientId: "", quantity: 0, unit: "" })}
					>
						<Plus />
						Adicionar ingrediente
					</Button>
				</div>

				{errors.ingredients &&
					typeof errors.ingredients.message === "string" && (
						<Alert variant="destructive">
							<AlertDescription>{errors.ingredients.message}</AlertDescription>
						</Alert>
					)}

				<div className="space-y-3 max-h-[400px] overflow-y-auto">
					{fields.map((field, index) => (
						<div
							key={field.id}
							className="flex gap-2 items-start p-3 border rounded-lg"
						>
							<div className="flex-1 space-y-2">
								<InputGroup>
									<Label>Ingrediente</Label>
									<Controller
										name={`ingredients.${index}.ingredientId`}
										control={control}
										render={({ field }) => (
											<Select
												onValueChange={field.onChange}
												value={field.value}
												defaultValue={field.value}
											>
												<SelectTrigger>
													<SelectValue placeholder="Selecione o ingrediente" />
												</SelectTrigger>
												<SelectContent>
													{ingredients?.map((ing) => (
														<SelectItem key={ing.id} value={ing.id}>
															{ing.name}
														</SelectItem>
													))}
												</SelectContent>
											</Select>
										)}
									/>
									{errors.ingredients?.[index]?.ingredientId && (
										<p className="text-sm text-destructive">
											{errors.ingredients[index]?.ingredientId?.message}
										</p>
									)}
								</InputGroup>

								<div className="grid grid-cols-2 gap-2">
									<InputGroup>
										<Label>Quantidade</Label>
										<Input
											type="number"
											step="0.01"
											{...register(`ingredients.${index}.quantity`, {
												valueAsNumber: true,
											})}
										/>
										{errors.ingredients?.[index]?.quantity && (
											<p className="text-sm text-destructive">
												{errors.ingredients[index]?.quantity?.message}
											</p>
										)}
									</InputGroup>

									<InputGroup>
										<Label>Unidade</Label>
										<Controller
											name={`ingredients.${index}.unit`}
											control={control}
											render={({ field }) => (
												<Select
													onValueChange={field.onChange}
													value={field.value}
													defaultValue={field.value}
												>
													<SelectTrigger>
														<SelectValue placeholder="Unidade" />
													</SelectTrigger>
													<SelectContent>
														{units?.map((unit) => (
															<SelectItem key={unit.name} value={unit.name}>
																{unit.name}
															</SelectItem>
														))}
													</SelectContent>
												</Select>
											)}
										/>
										{errors.ingredients?.[index]?.unit && (
											<p className="text-sm text-destructive">
												{errors.ingredients[index]?.unit?.message}
											</p>
										)}
									</InputGroup>
								</div>
							</div>

							{fields.length > 1 && (
								<Button
									type="button"
									variant="destructive"
									size="icon"
									onClick={() => remove(index)}
									className="mt-6"
								>
									<Trash2 />
								</Button>
							)}
						</div>
					))}
				</div>
			</div>

			{error && (
				<Alert variant="destructive">
					<AlertDescription>
						Erro ao cadastrar receita: {error?.message ?? "erro desconhecido"}
					</AlertDescription>
				</Alert>
			)}

			<Button type="submit" disabled={isPending}>
				{isPending
					? mode === "edit"
						? "Atualizando..."
						: "Cadastrando..."
					: mode === "edit"
						? "Atualizar receita"
						: "Cadastrar receita"}
			</Button>
		</form>
	);
}
