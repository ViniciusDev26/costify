import { create } from "zustand";
import { persist } from "zustand/middleware";
import type { Theme, ThemeState } from "./types";

export function toggleTheme(theme: Theme) {
	const newTheme = theme === "LIGHT" ? "DARK" : ("LIGHT" as Theme);

	return { theme: newTheme };
}

export const useThemeStore = create<ThemeState>()(
	persist(
		(set) => ({
			theme: "LIGHT",
			toggleTheme() {
				return set((state) => toggleTheme(state.theme));
			},
		}),
		{
			name: "THEME",
		},
	),
);
