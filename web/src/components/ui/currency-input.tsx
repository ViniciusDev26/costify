import * as React from "react";
import { cn } from "@/lib/utils";
import { Input } from "./input";

interface CurrencyInputProps
	extends Omit<React.ComponentProps<typeof Input>, "onChange" | "value"> {
	value?: number;
	onChange?: (value: number) => void;
}

export const CurrencyInput = React.forwardRef<
	HTMLInputElement,
	CurrencyInputProps
>(({ value, onChange, className, ...props }, ref) => {
	const [displayValue, setDisplayValue] = React.useState("");

	const formatCurrency = React.useCallback((val: number): string => {
		return new Intl.NumberFormat("pt-BR", {
			style: "currency",
			currency: "BRL",
		}).format(val);
	}, []);

	React.useEffect(() => {
		if (value !== undefined && value !== null) {
			setDisplayValue(formatCurrency(value));
		}
	}, [value, formatCurrency]);

	const parseCurrency = (val: string): number => {
		// Remove tudo exceto dígitos
		const numbers = val.replace(/\D/g, "");
		// Converte centavos para reais (divide por 100)
		return numbers ? Number.parseFloat(numbers) / 100 : 0;
	};

	const handleChange = (e: React.ChangeEvent<HTMLInputElement>) => {
		const inputValue = e.target.value;
		const numericValue = parseCurrency(inputValue);

		setDisplayValue(formatCurrency(numericValue));
		onChange?.(numericValue);
	};

	return (
		<Input
			{...props}
			ref={ref}
			type="text"
			value={displayValue}
			onChange={handleChange}
			className={cn(className)}
			placeholder="R$ 0,00"
		/>
	);
});

CurrencyInput.displayName = "CurrencyInput";
