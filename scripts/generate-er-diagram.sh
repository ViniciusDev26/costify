#!/bin/bash

# Exit on error
set -e

# Database connection details from environment variables
DB_HOST="${PGHOST:-localhost}"
DB_PORT="${PGPORT:-5432}"
DB_NAME="${PGDATABASE:-costify_db}"
DB_USER="${PGUSER:-costify_user}"
DB_PASS="${PGPASSWORD:-costify_pass}"

# Output file
OUTPUT_DIR="docs"
MERMAID_FILE="$OUTPUT_DIR/database-er-diagram.mmd"

# Create docs directory if it doesn't exist
mkdir -p "$OUTPUT_DIR"

# Function to execute psql queries
query_db() {
    PGPASSWORD="$DB_PASS" psql -h "$DB_HOST" -p "$DB_PORT" -U "$DB_USER" -d "$DB_NAME" -t -A -F"|" -c "$1"
}

echo "🎨 Generating Mermaid ER diagram..."

# Start Mermaid diagram
cat > "$MERMAID_FILE" << 'EOF'
erDiagram
EOF

# Get all ENUMs first and add them as entities
ENUMS=$(query_db "SELECT t.typname AS enum_name FROM pg_type t JOIN pg_catalog.pg_namespace n ON n.oid = t.typnamespace WHERE n.nspname = 'public' AND t.typtype = 'e' ORDER BY t.typname;")

if [ ! -z "$ENUMS" ] && [ "$ENUMS" != "" ]; then
    for enum_name in $ENUMS; do
        echo "" >> "$MERMAID_FILE"
        echo "    \"$enum_name (ENUM)\" {" >> "$MERMAID_FILE"

        # Get ENUM values
        ENUM_VALUES=$(query_db "SELECT e.enumlabel FROM pg_enum e JOIN pg_type t ON e.enumtypid = t.oid WHERE t.typname = '$enum_name' ORDER BY e.enumsortorder;")

        while IFS='|' read -r value; do
            echo "        $value string" >> "$MERMAID_FILE"
        done <<< "$ENUM_VALUES"

        echo "    }" >> "$MERMAID_FILE"
    done
fi

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
                WHEN data_type = 'numeric' AND numeric_precision IS NOT NULL AND numeric_scale IS NOT NULL AND numeric_scale > 0
                    THEN 'numeric(' || numeric_precision || ',' || numeric_scale || ')'
                WHEN data_type = 'numeric' AND numeric_precision IS NOT NULL
                    THEN 'numeric(' || numeric_precision || ')'
                WHEN data_type = 'numeric' THEN 'numeric'
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

    while IFS='|' read -r col_name data_type nullable col_default; do
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

        # Clean up data type for Mermaid compatibility (replace commas and spaces with underscores)
        data_type=$(echo "$data_type" | sed 's/NULL//g' | sed 's/,/_/g' | sed 's/ /_/g' | xargs)

        echo "        $col_name $data_type$KEY_INDICATOR" >> "$MERMAID_FILE"
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
    while IFS='|' read -r table_name foreign_table delete_rule; do
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

# Add relationships between tables and ENUMs
if [ ! -z "$ENUMS" ] && [ "$ENUMS" != "" ]; then
    echo "" >> "$MERMAID_FILE"

    # For each table, find columns that use ENUM types
    for table in $TABLES; do
        # Get columns that use ENUM types
        ENUM_COLUMNS=$(query_db "
            SELECT
                c.column_name,
                c.udt_name as enum_type
            FROM information_schema.columns c
            WHERE c.table_schema = 'public'
                AND c.table_name = '$table'
                AND c.data_type = 'USER-DEFINED'
            ORDER BY c.ordinal_position;
        ")

        if [ ! -z "$ENUM_COLUMNS" ] && [ "$ENUM_COLUMNS" != "" ]; then
            while IFS='|' read -r column_name enum_type; do
                # Add relationship: table uses enum
                echo "    $table }o--|| \"$enum_type (ENUM)\" : \"uses\"" >> "$MERMAID_FILE"
            done <<< "$ENUM_COLUMNS"
        fi
    done
fi

echo "✅ Mermaid diagram with ENUMs generated: $MERMAID_FILE"

# Update README with embedded diagram (only section between markers)
README_FILE="$OUTPUT_DIR/README.md"
echo "📝 Updating docs README..."

# Create the new diagram section
TEMP_DIAGRAM=$(mktemp)
cat > "$TEMP_DIAGRAM" << 'DIAGRAM_START'
<!-- ER_DIAGRAM_START -->
```mermaid
DIAGRAM_START

cat "$MERMAID_FILE" >> "$TEMP_DIAGRAM"

cat >> "$TEMP_DIAGRAM" << 'DIAGRAM_END'
```
<!-- ER_DIAGRAM_END -->
DIAGRAM_END

# Check if README exists and has markers
if [ -f "$README_FILE" ] && grep -q "<!-- ER_DIAGRAM_START -->" "$README_FILE" && grep -q "<!-- ER_DIAGRAM_END -->" "$README_FILE"; then
    # Replace diagram section
    sed -n '1,/<!-- ER_DIAGRAM_START -->/p' "$README_FILE" | sed '$d' > "$README_FILE.tmp"
    cat "$TEMP_DIAGRAM" >> "$README_FILE.tmp"
    sed -n '/<!-- ER_DIAGRAM_END -->/,$p' "$README_FILE" | tail -n +2 >> "$README_FILE.tmp"
    mv "$README_FILE.tmp" "$README_FILE"
    echo "✅ README updated (diagram section replaced): $README_FILE"
else
    # README doesn't exist or doesn't have markers - create it with full template
    cat > "$README_FILE" << 'HEADER'
# Documentation

This directory contains automatically generated project documentation.

## Database

### Entity Relationship Diagram

The ER diagram below represents the database schema with all tables, relationships, and constraints.

HEADER

    cat "$TEMP_DIAGRAM" >> "$README_FILE"

    cat >> "$README_FILE" << 'FOOTER'

### How It Works

The ER diagram is automatically generated by GitHub Actions on every commit to `main` that modifies database migration files.

**Workflow:**
1. Trigger on push to `main` with changes in `src/main/resources/db/migration/**`
2. PostgreSQL container is started and Flyway migrations are executed
3. ER diagram is generated from database metadata and embedded in README
4. Documentation is committed back to the repository with `[skip ci]`

**Manual Generation:**

```bash
# Ensure PostgreSQL is running
docker-compose up -d postgres

# Generate ER diagram (automatically updates README)
PGHOST=localhost PGPORT=5432 PGDATABASE=costify PGUSER=postgres PGPASSWORD=postgres \
  ./scripts/generate-er-diagram.sh
```

### Viewing the Diagram

- **GitHub**: The Mermaid diagram renders natively in GitHub's markdown viewer
- **VS Code**: Install "Markdown Preview Mermaid Support" extension
- **Other editors**: Any Mermaid-compatible markdown viewer

---

*This documentation is automatically updated by the CI/CD pipeline.*
FOOTER

    echo "✅ README created with markers: $README_FILE"
fi

# Cleanup
rm -f "$TEMP_DIAGRAM"
echo "🎉 ER diagram with ENUMs generation complete!"
