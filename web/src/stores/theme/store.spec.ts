import { describe, expect, it } from "vitest";
import { toggleTheme } from "./store";

describe(toggleTheme.name, () => {
	it("should toggle theme from LIGHT to DARK", () => {
		const result = toggleTheme("LIGHT");
		expect(result).toEqual({ theme: "DARK" });
	});

	it("should toggle theme from DARK to LIGHT", () => {
		const result = toggleTheme("DARK");
		expect(result).toEqual({ theme: "LIGHT" });
	});
});
