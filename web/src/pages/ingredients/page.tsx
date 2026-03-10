import { zodResolver } from "@hookform/resolvers/zod";
import { useMemo } from "react";
import { Controller, useForm } from "react-hook-form";
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
import { useRegisterIngredient } from "@/hooks/use-ingredients-query";
import { useUnits } from "@/hooks/use-units-query";
import {
	type CreateIngredientInput,
	CreateIngredientSchema,
} from "./schemas/create-ingredient.schema";

export function IngredientsPage() {
	const { data: unitsData } = useUnits();
	const { mutateAsync: registerIngredient } = useRegisterIngredient();
	const { register, handleSubmit, control } = useForm({
		resolver: zodResolver(CreateIngredientSchema),
	});

	const units = useMemo(() => {
		const units = unitsData?.map((unit) => unit.name);
		return units ?? [];
	}, [unitsData]);

	async function onSubmit(data: CreateIngredientInput) {
		await registerIngredient(data);
	}

	return (
		<div className="w-full h-screen flex items-center justify-center gap-2">
			<form className="flex flex-col gap-2" onSubmit={handleSubmit(onSubmit)}>
				<InputGroup>
					<Label htmlFor="name">Nome do ingrediente</Label>
					<Input id="name" {...register("name")} />
				</InputGroup>

				<InputGroup>
					<Label htmlFor="price">Preço do ingrediente</Label>
					<Input id="price" type="number" {...register("packagePrice")} />
				</InputGroup>

				<InputGroup>
					<Label>Quantidade de conteúdo do pacote</Label>
					<Input type="number" {...register("packageQuantity")} />
				</InputGroup>

				<InputGroup>
					<Label>Unidade de medida do pacote</Label>
					<Controller
						name="packageUnit"
						control={control}
						render={({ field }) => (
							<Select onValueChange={field.onChange} value={field.value}>
								<SelectTrigger>
									<SelectValue placeholder="Unidade" />
								</SelectTrigger>
								<SelectContent>
									{units?.map((un) => (
										<SelectItem key={un} value={un}>
											{un}
										</SelectItem>
									))}
								</SelectContent>
							</Select>
						)}
					/>
				</InputGroup>
				<Button type="submit">Register</Button>
			</form>
		</div>
	);
}
