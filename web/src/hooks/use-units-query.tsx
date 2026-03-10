import { useQuery } from "@tanstack/react-query";
import { unitsQueries } from "@/api/costify/queries/units";

export const UNITS_QUERY_KEYS = {
	all: ["units"] as const,
};

export function useUnits() {
	return useQuery({
		queryKey: UNITS_QUERY_KEYS.all,
		queryFn: unitsQueries.getUnits,
	});
}
