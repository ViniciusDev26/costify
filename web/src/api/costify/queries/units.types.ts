export type UnitType = "VOLUME" | "WEIGHT" | "UNIT";
export type UnitResponse = {
	name: string;
	type: UnitType;
	factorToBase: number;
};
