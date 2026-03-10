import { ArrowLeft } from "lucide-react";
import { Link, useNavigate, useParams } from "react-router-dom";
import { Alert, AlertDescription } from "@/components/ui/alert";
import { Button } from "@/components/ui/button";
import { useRecipe } from "@/hooks/use-recipes-query";
import { RecipeForm } from "../components/recipe-form";

export default function EditRecipePage() {
	const { id } = useParams<{ id: string }>();
	const navigate = useNavigate();
	const { data: recipe, isLoading, isError, error } = useRecipe(id || "");

	const handleSuccess = () => {
		navigate("/recipes");
	};

	return (
		<div className="mx-auto max-w-2xl p-6">
			<div className="mb-6 flex items-center gap-3">
				<Button variant="outline" size="icon" asChild>
					<Link to="/recipes">
						<ArrowLeft />
					</Link>
				</Button>
				<h1 className="text-2xl font-semibold">Editar Receita</h1>
			</div>

			{isLoading ? (
				<p>Carregando receita...</p>
			) : isError ? (
				<Alert variant="destructive">
					<AlertDescription>
						Erro ao carregar receita: {error?.message}
					</AlertDescription>
				</Alert>
			) : !recipe ? (
				<Alert variant="destructive">
					<AlertDescription>Receita não encontrada</AlertDescription>
				</Alert>
			) : (
				<div className="rounded-lg border p-6">
					<RecipeForm
						mode="edit"
						recipeId={id}
						initialData={recipe}
						onSuccess={handleSuccess}
					/>
				</div>
			)}
		</div>
	);
}
