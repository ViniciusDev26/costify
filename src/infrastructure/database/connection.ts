import postgres from 'postgres'
import { drizzle } from 'drizzle-orm/postgres-js'
import * as schema from './schema/index.js'

// Create PostgreSQL connection
const connectionString =
  process.env.DATABASE_URL || 'postgresql://costify:costify123@localhost:5432/costify_ts'

export const client = postgres(connectionString, {
  max: 10, // Connection pool size
  idle_timeout: 20,
  connect_timeout: 10,
})

// Create Drizzle database instance
export const db = drizzle(client, { schema })

export type Database = typeof db
