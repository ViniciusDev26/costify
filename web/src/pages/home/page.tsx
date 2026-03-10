import { ChefHat, UtensilsCrossed } from "lucide-react";
import { Link } from "react-router-dom";
import { Button } from "@/components/ui/button";

export default function HomePage() {
	return (
		<div className="w-full h-screen flex flex-col items-center justify-center gap-8">
			<div className="text-center">
				<h1 className="text-4xl font-bold mb-2">COSTIFY</h1>
				<p className="text-muted-foreground">
					Sistema de gestão de custos de receitas
				</p>
			</div>

			<div className="flex flex-col sm:flex-row gap-4">
				<Link to="/ingredients">
					<Button size="lg" className="min-w-[200px]">
						<UtensilsCrossed />
						Ingredientes
					</Button>
				</Link>
				<Link to="/recipes">
					<Button size="lg" variant="outline" className="min-w-[200px]">
						<ChefHat />
						Receitas
					</Button>
				</Link>
			</div>
		</div>
	);
}
