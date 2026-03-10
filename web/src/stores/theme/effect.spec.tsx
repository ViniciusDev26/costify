import { render } from "@testing-library/react";
import { beforeEach, describe, expect, it } from "vitest";
import { ThemeEffect } from "./effect";
import { useThemeStore } from "./store";

describe(ThemeEffect.name, () => {
	beforeEach(() => {
		useThemeStore.setState({ theme: "LIGHT" });
		document.documentElement.classList.remove("light", "dark");
	});

	it("should add light class to document root when theme is LIGHT", () => {
		useThemeStore.setState({ theme: "LIGHT" });

		render(<ThemeEffect />);

		const root = document.documentElement;
		expect(root.classList.contains("light")).toBe(true);
		expect(root.classList.contains("dark")).toBe(false);
	});

	it("should add dark class to document root when theme is DARK", () => {
		useThemeStore.setState({ theme: "DARK" });

		render(<ThemeEffect />);

		const root = document.documentElement;
		expect(root.classList.contains("dark")).toBe(true);
		expect(root.classList.contains("light")).toBe(false);
	});

	it("should remove existing theme classes before adding new one", () => {
		document.documentElement.classList.add("light");
		useThemeStore.setState({ theme: "DARK" });

		render(<ThemeEffect />);

		const root = document.documentElement;
		expect(root.classList.contains("light")).toBe(false);
		expect(root.classList.contains("dark")).toBe(true);
	});

	it("should render null", () => {
		useThemeStore.setState({ theme: "LIGHT" });

		const { container } = render(<ThemeEffect />);

		expect(container.firstChild).toBeNull();
	});
});
