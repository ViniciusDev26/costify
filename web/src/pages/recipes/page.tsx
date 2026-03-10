import { ArrowLeft, Pencil, Plus, RefreshCw } from "lucide-react";
import { useState } from "react";
import { Link } from "react-router-dom";
import { Alert, AlertDescription } from "@/components/ui/alert";
import { Button } from "@/components/ui/button";
import {
	Dialog,
	DialogContent,
	DialogHeader,
	DialogTitle,
	DialogTrigger,
} from "@/components/ui/dialog";
import {
	Table,
	TableBody,
	TableCell,
	TableHead,
	TableHeader,
	TableRow,
} from "@/components/ui/table";
import { useRecipesList } from "@/hooks/use-recipes-query";
import { RecipeForm } from "./components/recipe-form";

const currency = new Intl.NumberFormat("pt-BR", {
	style: "currency",
	currency: "BRL",
});

export default function RecipesListPage() {
	const [open, setOpen] = useState(false);
	const { data, isLoading, isError, error, refetch, isRefetching } =
		useRecipesList();

	return (
		<div className="mx-auto max-w-4xl p-6">
			<div className="mb-4 flex items-center justify-between">
				<div className="flex items-center gap-3">
					<Button variant="outline" size="icon" asChild>
						<Link to="/">
							<ArrowLeft />
						</Link>
					</Button>
					<h1 className="text-2xl font-semibold">Receitas</h1>
				</div>
				<div className="flex gap-2">
					<Button
						onClick={() => refetch()}
						variant="outline"
						disabled={isRefetching}
					>
						<RefreshCw className={isRefetching ? "animate-spin" : ""} />
						{isRefetching ? "Atualizando..." : "Atualizar"}
					</Button>
					<Dialog open={open} onOpenChange={setOpen}>
						<DialogTrigger asChild>
							<Button>
								<Plus />
								Cadastrar receita
							</Button>
						</DialogTrigger>
						<DialogContent className="max-w-2xl">
							<DialogHeader>
								<DialogTitle>Cadastrar nova receita</DialogTitle>
							</DialogHeader>
							<RecipeForm onSuccess={() => setOpen(false)} />
						</DialogContent>
					</Dialog>
				</div>
			</div>

			{isLoading ? (
				<p>Carregando...</p>
			) : isError ? (
				<Alert variant="destructive">
					<AlertDescription>
						Falha ao carregar receitas: {error?.message}
					</AlertDescription>
				</Alert>
			) : !data || data.length === 0 ? (
				<Alert>
					<AlertDescription>
						Nenhuma receita cadastrada. Quer criar agora?{" "}
						<button
							onClick={() => setOpen(true)}
							className="underline font-medium hover:text-primary"
						>
							Cadastrar receita
						</button>
					</AlertDescription>
				</Alert>
			) : (
				<div className="rounded-md border">
					<Table>
						<TableHeader>
							<TableRow>
								<TableHead>Nome</TableHead>
								<TableHead>Custo total</TableHead>
								<TableHead className="w-[100px]">Ações</TableHead>
							</TableRow>
						</TableHeader>
						<TableBody>
							{data.map((recipe) => (
								<TableRow key={recipe.id}>
									<TableCell className="font-medium">{recipe.name}</TableCell>
									<TableCell>
										{typeof recipe.totalCost === "number"
											? currency.format(recipe.totalCost)
											: "—"}
									</TableCell>
									<TableCell>
										<Button
											variant="ghost"
											size="icon"
											asChild
											title="Editar receita"
										>
											<Link to={`/recipes/${recipe.id}/edit`}>
												<Pencil className="h-4 w-4" />
											</Link>
										</Button>
									</TableCell>
								</TableRow>
							))}
						</TableBody>
					</Table>
				</div>
			)}
		</div>
	);
}
