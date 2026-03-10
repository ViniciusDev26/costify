import { client } from "../client";
import type { UnitResponse } from "./units.types";

async function getUnits() {
	const { data } = await client.get<UnitResponse[]>("/api/units");
	return data;
}

export const unitsQueries = {
	getUnits,
};
