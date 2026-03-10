import { z } from "zod";

const EnvSchema = z.object({
	VITE_COSTIFY_API_URL: z.url(),
});

export const env = EnvSchema.parse(import.meta.env);
console.log(import.meta.env, env);
