export type Theme = "LIGHT" | "DARK";
export interface ThemeState {
	theme: Theme;
	toggleTheme: () => void;
}
