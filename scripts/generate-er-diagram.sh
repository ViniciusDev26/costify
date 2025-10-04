#!/bin/bash

# Exit on error
set -e

# Database connection details from environment variables
DB_HOST="${PGHOST:-localhost}"
DB_PORT="${PGPORT:-5432}"
DB_NAME="${PGDATABASE:-costify_db}"
DB_USER="${PGUSER:-costify_user}"
DB_PASS="${PGPASSWORD:-costify_pass}"

# Output files
OUTPUT_DIR="docs"
MERMAID_FILE="$OUTPUT_DIR/database-er-diagram.mmd"
PNG_FILE="$OUTPUT_DIR/database-er-diagram.png"
SVG_FILE="$OUTPUT_DIR/database-er-diagram.svg"

# Create docs directory if it doesn't exist
mkdir -p "$OUTPUT_DIR"

# Function to execute psql queries
query_db() {
    PGPASSWORD="$DB_PASS" psql -h "$DB_HOST" -p "$DB_PORT" -U "$DB_USER" -d "$DB_NAME" -t -A -F"," -c "$1"
}

echo "🎨 Generating Mermaid ER diagram..."

# Start Mermaid diagram
cat > "$MERMAID_FILE" << 'EOF'
erDiagram
EOF

# Get all tables (excluding Flyway history)
TABLES=$(query_db "SELECT tablename FROM pg_tables WHERE schemaname = 'public' AND tablename != 'flyway_schema_history' ORDER BY tablename;")

# Generate table definitions
for table in $TABLES; do
    echo "" >> "$MERMAID_FILE"

    # Add table with columns
    echo "    $table {" >> "$MERMAID_FILE"

    # Get columns with their types and constraints
    COLUMNS=$(query_db "
        SELECT
            column_name,
            CASE
                WHEN data_type = 'character varying' THEN 'varchar(' || COALESCE(character_maximum_length::text, '') || ')'
                WHEN data_type = 'numeric' AND numeric_scale > 0 THEN 'numeric(' || numeric_precision || ',' || numeric_scale || ')'
                WHEN data_type = 'numeric' THEN 'numeric(' || numeric_precision || ')'
                WHEN data_type = 'integer' THEN 'integer'
                WHEN data_type = 'USER-DEFINED' THEN udt_name
                ELSE data_type
            END as full_type,
            is_nullable,
            column_default
        FROM information_schema.columns
        WHERE table_schema = 'public'
            AND table_name = '$table'
        ORDER BY ordinal_position;
    ")

    while IFS=',' read -r col_name data_type nullable col_default; do
        # Determine key type
        IS_PK=$(query_db "SELECT EXISTS (SELECT 1 FROM pg_index i JOIN pg_attribute a ON a.attrelid = i.indrelid AND a.attnum = ANY(i.indkey) WHERE i.indrelid = 'public.$table'::regclass AND i.indisprimary AND a.attname = '$col_name');")

        IS_FK=$(query_db "SELECT EXISTS (SELECT 1 FROM information_schema.key_column_usage kcu JOIN information_schema.table_constraints tc ON kcu.constraint_name = tc.constraint_name WHERE tc.constraint_type = 'FOREIGN KEY' AND tc.table_schema = 'public' AND tc.table_name = '$table' AND kcu.column_name = '$col_name');")

        IS_UNIQUE=$(query_db "SELECT EXISTS (SELECT 1 FROM information_schema.key_column_usage kcu JOIN information_schema.table_constraints tc ON kcu.constraint_name = tc.constraint_name WHERE tc.constraint_type = 'UNIQUE' AND tc.table_schema = 'public' AND tc.table_name = '$table' AND kcu.column_name = '$col_name');")

        # Build key indicator
        KEY_INDICATOR=""
        if [ "$IS_PK" = "t" ]; then
            KEY_INDICATOR=" PK"
        elif [ "$IS_FK" = "t" ]; then
            KEY_INDICATOR=" FK"
        elif [ "$IS_UNIQUE" = "t" ]; then
            KEY_INDICATOR=" UK"
        fi

        # Clean up data type for display
        data_type=$(echo "$data_type" | sed 's/NULL//g' | xargs)

        echo "        $data_type $col_name$KEY_INDICATOR" >> "$MERMAID_FILE"
    done <<< "$COLUMNS"

    echo "    }" >> "$MERMAID_FILE"
done

# Add relationships
echo "" >> "$MERMAID_FILE"

# Get foreign key relationships
FK_RELATIONSHIPS=$(query_db "
    SELECT DISTINCT
        tc.table_name,
        ccu.table_name AS foreign_table_name,
        rc.delete_rule
    FROM information_schema.table_constraints AS tc
    JOIN information_schema.key_column_usage AS kcu
        ON tc.constraint_name = kcu.constraint_name
        AND tc.table_schema = kcu.table_schema
    JOIN information_schema.constraint_column_usage AS ccu
        ON ccu.constraint_name = tc.constraint_name
        AND ccu.table_schema = tc.table_schema
    JOIN information_schema.referential_constraints AS rc
        ON rc.constraint_name = tc.constraint_name
    WHERE tc.constraint_type = 'FOREIGN KEY'
        AND tc.table_schema = 'public'
        AND tc.table_name != 'flyway_schema_history'
    ORDER BY tc.table_name;
")

if [ ! -z "$FK_RELATIONSHIPS" ] && [ "$FK_RELATIONSHIPS" != "" ]; then
    while IFS=',' read -r table_name foreign_table delete_rule; do
        # Determine relationship cardinality
        # For recipe_ingredients: recipes ||--o{ recipe_ingredients
        # recipes to recipe_ingredients: one-to-many
        # ingredients to recipe_ingredients: one-to-many

        if [ "$delete_rule" = "CASCADE" ]; then
            # Strong relationship (identifying)
            echo "    $foreign_table ||--o{ $table_name : \"has\"" >> "$MERMAID_FILE"
        else
            # Weak relationship (non-identifying)
            echo "    $foreign_table ||..o{ $table_name : \"references\"" >> "$MERMAID_FILE"
        fi
    done <<< "$FK_RELATIONSHIPS"
fi

echo "✅ Mermaid diagram generated: $MERMAID_FILE"

# Generate PNG and SVG from Mermaid
if command -v mmdc &> /dev/null; then
    echo "🖼️  Generating PNG diagram..."
    mmdc -i "$MERMAID_FILE" -o "$PNG_FILE" -t neutral -b transparent
    echo "✅ PNG diagram generated: $PNG_FILE"

    echo "🖼️  Generating SVG diagram..."
    mmdc -i "$MERMAID_FILE" -o "$SVG_FILE" -t neutral -b transparent
    echo "✅ SVG diagram generated: $SVG_FILE"
else
    echo "⚠️  Mermaid CLI (mmdc) not found. Skipping PNG/SVG generation."
    echo "   Install with: npm install -g @mermaid-js/mermaid-cli"
fi

echo ""
echo "🎉 ER diagram generation complete!"
