#!/bin/bash
# Test script for Costify API

BASE_URL="http://localhost:8080/api"

echo "================================"
echo "Costify API Test Script"
echo "================================"
echo ""

# Check if application is running
echo "1. Checking health..."
HEALTH=$(curl -s "$BASE_URL/actuator/health")
echo "Response: $HEALTH"
echo ""

# List available units
echo "2. Listing available units..."
curl -s "$BASE_URL/units" | python3 -m json.tool 2>/dev/null || curl -s "$BASE_URL/units"
echo ""
echo ""

# Create an ingredient
echo "3. Creating ingredient (Leite Integral)..."
INGREDIENT=$(curl -s -X POST "$BASE_URL/ingredients" \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Leite Integral",
    "packageQuantity": 1.0,
    "packagePrice": 5.50,
    "packageUnit": "L"
  }')
echo "Response: $INGREDIENT"
INGREDIENT_ID=$(echo $INGREDIENT | grep -o '"id":"[^"]*' | cut -d'"' -f4)
echo "Ingredient ID: $INGREDIENT_ID"
echo ""

# Create another ingredient
echo "4. Creating ingredient (Farinha de Trigo)..."
INGREDIENT2=$(curl -s -X POST "$BASE_URL/ingredients" \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Farinha de Trigo",
    "packageQuantity": 1.0,
    "packagePrice": 8.00,
    "packageUnit": "KG"
  }')
echo "Response: $INGREDIENT2"
INGREDIENT_ID2=$(echo $INGREDIENT2 | grep -o '"id":"[^"]*' | cut -d'"' -f4)
echo "Ingredient ID: $INGREDIENT_ID2"
echo ""

# List all ingredients
echo "5. Listing all ingredients..."
curl -s "$BASE_URL/ingredients" | python3 -m json.tool 2>/dev/null || curl -s "$BASE_URL/ingredients"
echo ""
echo ""

# Create a recipe if we have ingredient IDs
if [ ! -z "$INGREDIENT_ID" ] && [ ! -z "$INGREDIENT_ID2" ]; then
  echo "6. Creating recipe (Pão Caseiro)..."
  RECIPE=$(curl -s -X POST "$BASE_URL/recipes" \
    -H "Content-Type: application/json" \
    -d "{
      \"name\": \"Pão Caseiro\",
      \"ingredients\": [
        {
          \"ingredientId\": \"$INGREDIENT_ID\",
          \"quantity\": 300.0,
          \"unit\": \"ML\"
        },
        {
          \"ingredientId\": \"$INGREDIENT_ID2\",
          \"quantity\": 500.0,
          \"unit\": \"G\"
        }
      ]
    }")
  echo "Response: $RECIPE"
  RECIPE_ID=$(echo $RECIPE | grep -o '"id":"[^"]*' | cut -d'"' -f4)
  echo "Recipe ID: $RECIPE_ID"
  echo ""

  # Get recipe cost
  if [ ! -z "$RECIPE_ID" ]; then
    echo "7. Calculating recipe cost..."
    curl -s "$BASE_URL/recipes/$RECIPE_ID/cost" | python3 -m json.tool 2>/dev/null || curl -s "$BASE_URL/recipes/$RECIPE_ID/cost"
    echo ""
    echo ""
  fi

  # List all recipes
  echo "8. Listing all recipes..."
  curl -s "$BASE_URL/recipes" | python3 -m json.tool 2>/dev/null || curl -s "$BASE_URL/recipes"
  echo ""
  echo ""
fi

echo "================================"
echo "Test completed!"
echo "================================"
