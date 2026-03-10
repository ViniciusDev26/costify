import type { ReactNode } from "react";

export function InputGroup({
	children,
}: {
	children: ReactNode;
	error?: string;
}) {
	return <div className="flex flex-col gap-2">{children}</div>;
}
