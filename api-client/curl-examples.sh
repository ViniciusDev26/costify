#!/bin/bash

# Costify API - Curl Examples
# Command-line HTTP requests for testing the Costify REST API
# Usage: ./curl-examples.sh [function-name]
# Example: ./curl-examples.sh register_ingredients

set -e  # Exit on any error

# Configuration
BASE_URL="http://localhost:8080"
CONTENT_TYPE="application/json"

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# Helper function for pretty printing
print_header() {
    echo -e "${BLUE}================================================${NC}"
    echo -e "${BLUE}$1${NC}"
    echo -e "${BLUE}================================================${NC}"
}

print_success() {
    echo -e "${GREEN}✅ $1${NC}"
}

print_error() {
    echo -e "${RED}❌ $1${NC}"
}

print_info() {
    echo -e "${YELLOW}ℹ️  $1${NC}"
}

# Check if application is running
check_application() {
    print_header "Checking Application Status"
    
    if curl -sf "$BASE_URL/actuator/health" > /dev/null 2>&1; then
        print_success "Application is running at $BASE_URL"
    else
        print_error "Application is not running at $BASE_URL"
        print_info "Start the application with: ./mvnw spring-boot:run"
        exit 1
    fi
}

# Ingredient Registration Functions
register_flour() {
    print_header "Registering Flour Ingredient"
    
    response=$(curl -s -w "%{http_code}" \
        -X POST "$BASE_URL/ingredients" \
        -H "Content-Type: $CONTENT_TYPE" \
        -d '{
            "name": "Flour",
            "packageQuantity": 1.0,
            "packagePrice": 2.50,
            "packageUnit": "KG"
        }')
    
    http_code="${response: -3}"
    body="${response%???}"
    
    if [ "$http_code" = "200" ] || [ "$http_code" = "201" ]; then
        print_success "Flour registered successfully"
        echo "$body" | python3 -m json.tool 2>/dev/null || echo "$body"
        
        # Extract and save ingredient ID for later use
        ingredient_id=$(echo "$body" | python3 -c "import sys, json; print(json.load(sys.stdin)['id'])" 2>/dev/null || echo "")
        if [ -n "$ingredient_id" ]; then
            echo "$ingredient_id" > .flour_id
            print_info "Flour ID saved to .flour_id: $ingredient_id"
        fi
    else
        print_error "Failed to register flour (HTTP $http_code)"
        echo "$body"
    fi
}

register_sugar() {
    print_header "Registering Sugar Ingredient"
    
    response=$(curl -s -w "%{http_code}" \
        -X POST "$BASE_URL/ingredients" \
        -H "Content-Type: $CONTENT_TYPE" \
        -d '{
            "name": "Sugar",
            "packageQuantity": 1.0,
            "packagePrice": 1.80,
            "packageUnit": "KG"
        }')
    
    http_code="${response: -3}"
    body="${response%???}"
    
    if [ "$http_code" = "200" ] || [ "$http_code" = "201" ]; then
        print_success "Sugar registered successfully"
        echo "$body" | python3 -m json.tool 2>/dev/null || echo "$body"
        
        # Extract and save ingredient ID
        ingredient_id=$(echo "$body" | python3 -c "import sys, json; print(json.load(sys.stdin)['id'])" 2>/dev/null || echo "")
        if [ -n "$ingredient_id" ]; then
            echo "$ingredient_id" > .sugar_id
            print_info "Sugar ID saved to .sugar_id: $ingredient_id"
        fi
    else
        print_error "Failed to register sugar (HTTP $http_code)"
        echo "$body"
    fi
}

register_milk() {
    print_header "Registering Milk Ingredient"
    
    response=$(curl -s -w "%{http_code}" \
        -X POST "$BASE_URL/ingredients" \
        -H "Content-Type: $CONTENT_TYPE" \
        -d '{
            "name": "Milk",
            "packageQuantity": 1.0,
            "packagePrice": 3.20,
            "packageUnit": "L"
        }')
    
    http_code="${response: -3}"
    body="${response%???}"
    
    if [ "$http_code" = "200" ] || [ "$http_code" = "201" ]; then
        print_success "Milk registered successfully"
        echo "$body" | python3 -m json.tool 2>/dev/null || echo "$body"
        
        ingredient_id=$(echo "$body" | python3 -c "import sys, json; print(json.load(sys.stdin)['id'])" 2>/dev/null || echo "")
        if [ -n "$ingredient_id" ]; then
            echo "$ingredient_id" > .milk_id
            print_info "Milk ID saved to .milk_id: $ingredient_id"
        fi
    else
        print_error "Failed to register milk (HTTP $http_code)"
        echo "$body"
    fi
}

register_eggs() {
    print_header "Registering Eggs Ingredient"
    
    response=$(curl -s -w "%{http_code}" \
        -X POST "$BASE_URL/ingredients" \
        -H "Content-Type: $CONTENT_TYPE" \
        -d '{
            "name": "Eggs",
            "packageQuantity": 12.0,
            "packagePrice": 4.50,
            "packageUnit": "UN"
        }')
    
    http_code="${response: -3}"
    body="${response%???}"
    
    if [ "$http_code" = "200" ] || [ "$http_code" = "201" ]; then
        print_success "Eggs registered successfully"
        echo "$body" | python3 -m json.tool 2>/dev/null || echo "$body"
        
        ingredient_id=$(echo "$body" | python3 -c "import sys, json; print(json.load(sys.stdin)['id'])" 2>/dev/null || echo "")
        if [ -n "$ingredient_id" ]; then
            echo "$ingredient_id" > .eggs_id
            print_info "Eggs ID saved to .eggs_id: $ingredient_id"
        fi
    else
        print_error "Failed to register eggs (HTTP $http_code)"
        echo "$body"
    fi
}

register_salt() {
    print_header "Registering Salt Ingredient"
    
    response=$(curl -s -w "%{http_code}" \
        -X POST "$BASE_URL/ingredients" \
        -H "Content-Type: $CONTENT_TYPE" \
        -d '{
            "name": "Salt",
            "packageQuantity": 500.0,
            "packagePrice": 1.25,
            "packageUnit": "G"
        }')
    
    http_code="${response: -3}"
    body="${response%???}"
    
    if [ "$http_code" = "200" ] || [ "$http_code" = "201" ]; then
        print_success "Salt registered successfully"
        echo "$body" | python3 -m json.tool 2>/dev/null || echo "$body"
        
        ingredient_id=$(echo "$body" | python3 -c "import sys, json; print(json.load(sys.stdin)['id'])" 2>/dev/null || echo "")
        if [ -n "$ingredient_id" ]; then
            echo "$ingredient_id" > .salt_id
            print_info "Salt ID saved to .salt_id: $ingredient_id"
        fi
    else
        print_error "Failed to register salt (HTTP $http_code)"
        echo "$body"
    fi
}

# Combined function to register all ingredients
register_ingredients() {
    print_header "Registering All Ingredients"
    
    register_flour
    echo
    register_sugar
    echo
    register_milk
    echo
    register_eggs
    echo
    register_salt
    
    print_success "All ingredients registered!"
}

# Recipe Registration Functions
register_bread_recipe() {
    print_header "Registering Bread Recipe"
    
    # Check if ingredient IDs are available
    if [ ! -f .flour_id ] || [ ! -f .milk_id ] || [ ! -f .salt_id ]; then
        print_error "Ingredient IDs not found. Please register ingredients first."
        print_info "Run: ./curl-examples.sh register_ingredients"
        return 1
    fi
    
    flour_id=$(cat .flour_id)
    milk_id=$(cat .milk_id)
    salt_id=$(cat .salt_id)
    
    response=$(curl -s -w "%{http_code}" \
        -X POST "$BASE_URL/recipes" \
        -H "Content-Type: $CONTENT_TYPE" \
        -d "{
            \"name\": \"Simple Bread\",
            \"ingredients\": [
                {
                    \"ingredientId\": \"$flour_id\",
                    \"quantity\": 0.5,
                    \"unit\": \"KG\"
                },
                {
                    \"ingredientId\": \"$milk_id\",
                    \"quantity\": 200.0,
                    \"unit\": \"ML\"
                },
                {
                    \"ingredientId\": \"$salt_id\",
                    \"quantity\": 5.0,
                    \"unit\": \"G\"
                }
            ]
        }")
    
    http_code="${response: -3}"
    body="${response%???}"
    
    if [ "$http_code" = "200" ] || [ "$http_code" = "201" ]; then
        print_success "Bread recipe registered successfully"
        echo "$body" | python3 -m json.tool 2>/dev/null || echo "$body"
    else
        print_error "Failed to register bread recipe (HTTP $http_code)"
        echo "$body"
    fi
}

register_cake_recipe() {
    print_header "Registering Cake Recipe"
    
    # Check if ingredient IDs are available
    if [ ! -f .flour_id ] || [ ! -f .sugar_id ] || [ ! -f .milk_id ] || [ ! -f .eggs_id ]; then
        print_error "Ingredient IDs not found. Please register ingredients first."
        print_info "Run: ./curl-examples.sh register_ingredients"
        return 1
    fi
    
    flour_id=$(cat .flour_id)
    sugar_id=$(cat .sugar_id)
    milk_id=$(cat .milk_id)
    eggs_id=$(cat .eggs_id)
    
    response=$(curl -s -w "%{http_code}" \
        -X POST "$BASE_URL/recipes" \
        -H "Content-Type: $CONTENT_TYPE" \
        -d "{
            \"name\": \"Vanilla Cake\",
            \"ingredients\": [
                {
                    \"ingredientId\": \"$flour_id\",
                    \"quantity\": 0.3,
                    \"unit\": \"KG\"
                },
                {
                    \"ingredientId\": \"$sugar_id\",
                    \"quantity\": 0.2,
                    \"unit\": \"KG\"
                },
                {
                    \"ingredientId\": \"$milk_id\",
                    \"quantity\": 250.0,
                    \"unit\": \"ML\"
                },
                {
                    \"ingredientId\": \"$eggs_id\",
                    \"quantity\": 2.0,
                    \"unit\": \"UN\"
                }
            ]
        }")
    
    http_code="${response: -3}"
    body="${response%???}"
    
    if [ "$http_code" = "200" ] || [ "$http_code" = "201" ]; then
        print_success "Cake recipe registered successfully"
        echo "$body" | python3 -m json.tool 2>/dev/null || echo "$body"
    else
        print_error "Failed to register cake recipe (HTTP $http_code)"
        echo "$body"
    fi
}

# Validation Tests
test_validations() {
    print_header "Running Validation Tests"
    
    print_info "Testing invalid ingredient - missing name..."
    curl -s -X POST "$BASE_URL/ingredients" \
        -H "Content-Type: $CONTENT_TYPE" \
        -d '{
            "packageQuantity": 1.0,
            "packagePrice": 2.50,
            "packageUnit": "KG"
        }' | python3 -m json.tool 2>/dev/null || echo "Failed to parse response"
    
    echo
    print_info "Testing invalid ingredient - negative price..."
    curl -s -X POST "$BASE_URL/ingredients" \
        -H "Content-Type: $CONTENT_TYPE" \
        -d '{
            "name": "Invalid Ingredient",
            "packageQuantity": 1.0,
            "packagePrice": -1.0,
            "packageUnit": "KG"
        }' | python3 -m json.tool 2>/dev/null || echo "Failed to parse response"
    
    echo
    print_info "Testing invalid recipe - empty ingredients..."
    curl -s -X POST "$BASE_URL/recipes" \
        -H "Content-Type: $CONTENT_TYPE" \
        -d '{
            "name": "Empty Recipe",
            "ingredients": []
        }' | python3 -m json.tool 2>/dev/null || echo "Failed to parse response"
}

# Full workflow function
run_full_workflow() {
    print_header "Running Complete API Workflow"
    
    check_application
    echo
    
    register_ingredients
    echo
    
    register_bread_recipe
    echo
    
    register_cake_recipe
    echo
    
    test_validations
    
    print_success "Complete workflow finished!"
}

# Cleanup function
cleanup() {
    print_header "Cleaning Up Temporary Files"
    
    rm -f .flour_id .sugar_id .milk_id .eggs_id .salt_id
    print_success "Cleanup completed"
}

# Help function
show_help() {
    echo "Costify API - Curl Examples"
    echo "Usage: $0 [function]"
    echo
    echo "Available functions:"
    echo "  check_application     - Check if the application is running"
    echo "  register_flour        - Register flour ingredient"
    echo "  register_sugar        - Register sugar ingredient"
    echo "  register_milk         - Register milk ingredient"
    echo "  register_eggs         - Register eggs ingredient"
    echo "  register_salt         - Register salt ingredient"
    echo "  register_ingredients  - Register all ingredients"
    echo "  register_bread_recipe - Register bread recipe"
    echo "  register_cake_recipe  - Register cake recipe"
    echo "  test_validations      - Run validation tests"
    echo "  run_full_workflow     - Execute complete workflow"
    echo "  cleanup              - Remove temporary files"
    echo "  help                 - Show this help message"
    echo
    echo "Examples:"
    echo "  $0                    # Run full workflow"
    echo "  $0 register_ingredients"
    echo "  $0 register_bread_recipe"
    echo "  $0 cleanup"
}

# Main execution
main() {
    case "${1:-run_full_workflow}" in
        "check_application"|"check")
            check_application
            ;;
        "register_flour"|"flour")
            register_flour
            ;;
        "register_sugar"|"sugar")
            register_sugar
            ;;
        "register_milk"|"milk")
            register_milk
            ;;
        "register_eggs"|"eggs")
            register_eggs
            ;;
        "register_salt"|"salt")
            register_salt
            ;;
        "register_ingredients"|"ingredients")
            register_ingredients
            ;;
        "register_bread_recipe"|"bread")
            register_bread_recipe
            ;;
        "register_cake_recipe"|"cake")
            register_cake_recipe
            ;;
        "test_validations"|"validate")
            test_validations
            ;;
        "run_full_workflow"|"workflow"|"")
            run_full_workflow
            ;;
        "cleanup"|"clean")
            cleanup
            ;;
        "help"|"--help"|"-h")
            show_help
            ;;
        *)
            print_error "Unknown function: $1"
            echo
            show_help
            exit 1
            ;;
    esac
}

# Execute main function with all arguments
main "$@"