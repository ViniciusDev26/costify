# Costify API Client

This folder contains HTTP request configurations for testing the Costify REST API with various HTTP clients.

## Available Files

### 🔗 `costify-api.http`
Complete HTTP request collection compatible with:
- **IntelliJ IDEA** - Built-in HTTP client
- **VS Code** - REST Client extension
- **WebStorm/PyCharm** - JetBrains HTTP client
- **Other IDEs** with HTTP client support

### 📋 `postman-collection.json`
Postman collection with organized folders and environment variables for easy API testing.

### 🐚 `curl-examples.sh`  
Shell script with curl commands for command-line testing and CI/CD integration.

## Getting Started

### Prerequisites
1. **Start the application:**
   ```bash
   cd ..
   ./mvnw spring-boot:run
   ```

2. **Verify the application is running:**
   ```bash
   curl -f http://localhost:8080/actuator/health || echo "Application not ready"
   ```

### Using the HTTP Files

#### Option 1: IntelliJ IDEA / WebStorm
1. Open `costify-api.http` in your IDE
2. Click the ▶️ button next to any request
3. View responses in the integrated tool window

#### Option 2: VS Code with REST Client
1. Install the [REST Client extension](https://marketplace.visualstudio.com/items?itemName=humao.rest-client)
2. Open `costify-api.http`
3. Click "Send Request" above any request

#### Option 3: Command Line with curl
1. Make the shell script executable:
   ```bash
   chmod +x curl-examples.sh
   ```
2. Run individual commands or the entire script:
   ```bash
   ./curl-examples.sh
   ```

#### Option 4: Postman
1. Import `postman-collection.json` into Postman
2. Set up the environment variables if needed
3. Execute requests from the organized folders

## API Testing Workflow

### Step 1: Register Ingredients
Start by registering ingredients to get their UUIDs:

```http
POST /ingredients
{
  "name": "Flour",
  "packageQuantity": 1.0,
  "packagePrice": 2.50, 
  "packageUnit": "KG"
}
```

**Copy the `id` from the response** - you'll need it for recipe registration.

### Step 2: Update Recipe Requests
Replace the placeholder UUIDs in recipe requests with actual ingredient IDs:

```http
POST /recipes
{
  "name": "Bread",
  "ingredients": [
    {
      "ingredientId": "your-actual-ingredient-uuid-here",
      "quantity": 0.5,
      "unit": "KG"
    }
  ]
}
```

### Step 3: Test Validation
Try the invalid request examples to verify proper error handling.

## Supported Units

| Type | Units | Description |
|------|-------|-------------|
| **Volume** | `ML`, `L` | Milliliters, Liters |
| **Weight** | `G`, `KG` | Grams, Kilograms |
| **Unit** | `UN` | Units/Pieces |

## Error Responses

The API returns structured error responses for validation failures:

```json
{
  "timestamp": "2025-01-15T10:30:00",
  "status": 400,
  "error": "Bad Request",
  "message": "Validation failed",
  "path": "/ingredients"
}
```

## Environment Variables

For Postman or other tools that support environment variables:

| Variable | Value | Description |
|----------|-------|-------------|
| `baseUrl` | `http://localhost:8080` | Application base URL |
| `contentType` | `application/json` | Request content type |

## Troubleshooting

### Application Not Running
```bash
# Check if the application is running
curl http://localhost:8080/actuator/health

# Start the application if needed
cd .. && ./mvnw spring-boot:run
```

### Database Connection Issues
```bash
# Start PostgreSQL if needed
docker-compose up -d postgres

# Check database status
docker-compose ps
```

### Invalid UUID Errors
- Ensure you copy actual UUIDs from ingredient registration responses
- UUIDs must be valid v4 format (e.g., `550e8400-e29b-41d4-a716-446655440000`)

## Development Tips

1. **Use Variables**: Leverage HTTP client variables to avoid hardcoding values
2. **Save Responses**: Copy ingredient IDs from responses for use in recipe requests  
3. **Test Validation**: Run invalid examples to understand error handling
4. **Check Logs**: Monitor application logs for debugging information