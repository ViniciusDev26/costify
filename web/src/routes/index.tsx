import { createBrowserRouter } from "react-router-dom";
import HomePage from "@/pages/home/page";
import EditIngredientPage from "@/pages/ingredients/edit/page";
import IngredientsListPage from "@/pages/ingredients/list/page";
import EditRecipePage from "@/pages/recipes/edit/page";
import RecipesListPage from "@/pages/recipes/page";

export const router = createBrowserRouter([
	{
		path: "/",
		Component: HomePage,
	},
	{
		path: "/ingredients",
		Component: IngredientsListPage,
	},
	{
		path: "/ingredients/:id/edit",
		Component: EditIngredientPage,
	},
	{
		path: "/recipes",
		Component: RecipesListPage,
	},
	{
		path: "/recipes/:id/edit",
		Component: EditRecipePage,
	},
]);
