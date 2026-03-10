import axios from "axios";
import { env } from "@/env";

export const client = axios.create({
	baseURL: env.VITE_COSTIFY_API_URL,
});
