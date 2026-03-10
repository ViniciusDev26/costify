import { useEffect } from "react";
import { useThemeStore } from "./store";

export function ThemeEffect() {
	const theme = useThemeStore((s) => s.theme);

	useEffect(() => {
		const root = window.document.documentElement;
		root.classList.remove("light", "dark");
		root.classList.add(theme.toLowerCase());
	}, [theme]);

	return null;
}
