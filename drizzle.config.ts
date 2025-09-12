import { defineConfig } from 'drizzle-kit'

export default defineConfig({
  dialect: 'postgresql',
  schema: './src/infrastructure/database/schema/index.ts',
  out: './src/infrastructure/database/migrations',
  dbCredentials: {
    url: process.env.DATABASE_URL || 'postgresql://costify:costify123@localhost:5432/costify_ts?schema=public',
  },
  verbose: true,
  strict: true,
})