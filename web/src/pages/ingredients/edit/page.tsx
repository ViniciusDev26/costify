import { ArrowLeft } from "lucide-react";
import { Link, useNavigate, useParams } from "react-router-dom";
import { Alert, AlertDescription } from "@/components/ui/alert";
import { Button } from "@/components/ui/button";
import { useIngredient } from "@/hooks/use-ingredients-query";
import { IngredientForm } from "../components/ingredient-form";

export default function EditIngredientPage() {
	const { id } = useParams<{ id: string }>();
	const navigate = useNavigate();
	const {
		data: ingredient,
		isLoading,
		isError,
		error,
	} = useIngredient(id || "");

	const handleSuccess = () => {
		navigate("/ingredients");
	};

	return (
		<div className="mx-auto max-w-2xl p-6">
			<div className="mb-6 flex items-center gap-3">
				<Button variant="outline" size="icon" asChild>
					<Link to="/ingredients">
						<ArrowLeft />
					</Link>
				</Button>
				<h1 className="text-2xl font-semibold">Editar Ingrediente</h1>
			</div>

			{isLoading ? (
				<p>Carregando ingrediente...</p>
			) : isError ? (
				<Alert variant="destructive">
					<AlertDescription>
						Erro ao carregar ingrediente: {error?.message}
					</AlertDescription>
				</Alert>
			) : !ingredient ? (
				<Alert variant="destructive">
					<AlertDescription>Ingrediente não encontrado</AlertDescription>
				</Alert>
			) : (
				<div className="rounded-lg border p-6">
					<IngredientForm
						mode="edit"
						ingredientId={id}
						initialData={ingredient}
						onSuccess={handleSuccess}
					/>
				</div>
			)}
		</div>
	);
}
