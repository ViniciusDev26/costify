import { migrate } from 'drizzle-orm/postgres-js/migrator'
import { client, db } from './connection.js'

async function runMigrations() {
  console.log('🔄 Running database migrations...')

  try {
    await migrate(db, {
      migrationsFolder: './src/infrastructure/database/migrations',
    })

    console.log('✅ Database migrations completed successfully!')
  } catch (error) {
    console.error('❌ Migration failed:', error)
    process.exit(1)
  } finally {
    await client.end()
  }
}

if (import.meta.main) {
  await runMigrations()
}
