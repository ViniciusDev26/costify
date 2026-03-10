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
import { useIngredientsList } from "@/hooks/use-ingredients-query";
import { IngredientForm } from "../components/ingredient-form";

const currency = new Intl.NumberFormat("pt-BR", {
	style: "currency",
	currency: "BRL",
});

export default function IngredientsListPage() {
	const [open, setOpen] = useState(false);
	const { data, isLoading, isError, error, refetch, isRefetching } =
		useIngredientsList();

	return (
		<div className="mx-auto max-w-4xl p-6">
			<div className="mb-4 flex items-center justify-between">
				<div className="flex items-center gap-3">
					<Button variant="outline" size="icon" asChild>
						<Link to="/">
							<ArrowLeft />
						</Link>
					</Button>
					<h1 className="text-2xl font-semibold">Ingredientes</h1>
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
								Cadastrar ingrediente
							</Button>
						</DialogTrigger>
						<DialogContent>
							<DialogHeader>
								<DialogTitle>Cadastrar novo ingrediente</DialogTitle>
							</DialogHeader>
							<IngredientForm onSuccess={() => setOpen(false)} />
						</DialogContent>
					</Dialog>
				</div>
			</div>

			{isLoading ? (
				<p>Carregando...</p>
			) : isError ? (
				<Alert variant="destructive">
					<AlertDescription>
						Falha ao carregar ingredientes: {error?.message}
					</AlertDescription>
				</Alert>
			) : !data || data.length === 0 ? (
				<Alert>
					<AlertDescription>
						Nenhum ingrediente cadastrado. Quer criar agora?{" "}
						<button
							onClick={() => setOpen(true)}
							className="underline font-medium hover:text-primary"
						>
							Cadastrar ingrediente
						</button>
					</AlertDescription>
				</Alert>
			) : (
				<div className="rounded-md border">
					<Table>
						<TableHeader>
							<TableRow>
								<TableHead>Nome</TableHead>
								<TableHead>Pacote</TableHead>
								<TableHead>Preço do pacote</TableHead>
								<TableHead>Custo unitário</TableHead>
								<TableHead className="w-[100px]">Ações</TableHead>
							</TableRow>
						</TableHeader>
						<TableBody>
							{data.map((ing) => (
								<TableRow key={ing.id}>
									<TableCell className="font-medium">{ing.name}</TableCell>
									<TableCell>
										{ing.packageQuantity} {ing.packageUnit}
									</TableCell>
									<TableCell>{currency.format(ing.packagePrice)}</TableCell>
									<TableCell>
										{typeof ing.unitCost === "number"
											? currency.format(ing.unitCost)
											: "—"}
									</TableCell>
									<TableCell>
										<Button
											variant="ghost"
											size="icon"
											asChild
											title="Editar ingrediente"
										>
											<Link to={`/ingredients/${ing.id}/edit`}>
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
