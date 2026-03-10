import { StrictMode } from "react";
import { createRoot } from "react-dom/client";
import "./index.css";
import { QueryClientProvider } from "@tanstack/react-query";
import { RouterProvider } from "react-router-dom";
import { queryClient } from "./api/query-client.ts";
import { ThemeToggle } from "./components/theme-toggle.tsx";
import { router } from "./routes/index.tsx";
import { ThemeEffect } from "./stores/theme/effect.tsx";

createRoot(document.getElementById("root")!).render(
	<StrictMode>
		<ThemeEffect />
		<QueryClientProvider client={queryClient}>
			<RouterProvider router={router} />
			<ThemeToggle />
		</QueryClientProvider>
	</StrictMode>,
);
