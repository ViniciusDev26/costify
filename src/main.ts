import { Elysia } from 'elysia'
import { cors } from '@elysiajs/cors'
import { AppConfig } from './infrastructure/config/AppConfig.js'
import { errorHandler } from './infrastructure/middleware/ErrorHandler.js'

const app = new Elysia()
const config = AppConfig.getInstance()

// Configure CORS
app.use(
  cors({
    origin: true,
    methods: ['GET', 'POST', 'PUT', 'DELETE'],
    allowedHeaders: ['Content-Type', 'Authorization'],
  })
)

// Add error handler
app.use(errorHandler)

// Health check endpoint
app.get('/health', () => ({
  status: 'healthy',
  timestamp: new Date().toISOString(),
  uptime: process.uptime(),
}))

// API routes
app.group('/api/v1', (app) =>
  app.use(config.ingredientController.routes()).use(config.recipeController.routes())
)

// Handle graceful shutdown
process.on('SIGTERM', async () => {
  console.log('SIGTERM received, shutting down gracefully...')
  await config.close()
  process.exit(0)
})

process.on('SIGINT', async () => {
  console.log('SIGINT received, shutting down gracefully...')
  await config.close()
  process.exit(0)
})

const port = process.env.PORT || 3000

app.listen(port, () => {
  console.log(`🚀 Costify API is running on http://localhost:${port}`)
  console.log(`📚 Health check available at http://localhost:${port}/health`)
  console.log(`📊 API endpoints available at http://localhost:${port}/api/v1`)
})
