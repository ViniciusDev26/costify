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
OUTPUT_FILE="$OUTPUT_DIR/database-schema.md"

# Create docs directory if it doesn't exist
mkdir -p "$OUTPUT_DIR"

# Function to execute psql queries
query_db() {
    PGPASSWORD="$DB_PASS" psql -h "$DB_HOST" -p "$DB_PORT" -U "$DB_USER" -d "$DB_NAME" -t -A -F"," -c "$1"
}

# Start generating documentation
echo "# Database Schema Documentation" > "$OUTPUT_FILE"
echo "" >> "$OUTPUT_FILE"
echo "**Generated:** $(date '+%Y-%m-%d %H:%M:%S UTC')" >> "$OUTPUT_FILE"
echo "" >> "$OUTPUT_FILE"
echo "**Database:** $DB_NAME" >> "$OUTPUT_FILE"
echo "" >> "$OUTPUT_FILE"

# Get PostgreSQL version
PG_VERSION=$(query_db "SELECT version();")
echo "**PostgreSQL Version:** $PG_VERSION" >> "$OUTPUT_FILE"
echo "" >> "$OUTPUT_FILE"

# Table of Contents
echo "## Table of Contents" >> "$OUTPUT_FILE"
echo "" >> "$OUTPUT_FILE"

TABLES=$(query_db "SELECT tablename FROM pg_tables WHERE schemaname = 'public' ORDER BY tablename;")

for table in $TABLES; do
    echo "- [$table](#$table)" >> "$OUTPUT_FILE"
done

echo "" >> "$OUTPUT_FILE"
echo "---" >> "$OUTPUT_FILE"
echo "" >> "$OUTPUT_FILE"

# Generate documentation for each table
for table in $TABLES; do
    echo "## $table" >> "$OUTPUT_FILE"
    echo "" >> "$OUTPUT_FILE"

    # Get table comment
    TABLE_COMMENT=$(query_db "SELECT obj_description('public.$table'::regclass, 'pg_class');")
    if [ ! -z "$TABLE_COMMENT" ] && [ "$TABLE_COMMENT" != "" ]; then
        echo "**Description:** $TABLE_COMMENT" >> "$OUTPUT_FILE"
        echo "" >> "$OUTPUT_FILE"
    fi

    # Column information
    echo "### Columns" >> "$OUTPUT_FILE"
    echo "" >> "$OUTPUT_FILE"
    echo "| Column | Type | Nullable | Default | Description |" >> "$OUTPUT_FILE"
    echo "|--------|------|----------|---------|-------------|" >> "$OUTPUT_FILE"

    COLUMNS=$(query_db "
        SELECT
            column_name,
            data_type,
            character_maximum_length,
            numeric_precision,
            numeric_scale,
            is_nullable,
            column_default
        FROM information_schema.columns
        WHERE table_schema = 'public'
            AND table_name = '$table'
        ORDER BY ordinal_position;
    ")

    while IFS=',' read -r col_name data_type char_max num_prec num_scale nullable col_default; do
        # Build type with precision/length
        if [ ! -z "$char_max" ] && [ "$char_max" != "" ]; then
            full_type="$data_type($char_max)"
        elif [ ! -z "$num_prec" ] && [ "$num_prec" != "" ]; then
            if [ ! -z "$num_scale" ] && [ "$num_scale" != "" ] && [ "$num_scale" != "0" ]; then
                full_type="$data_type($num_prec,$num_scale)"
            else
                full_type="$data_type($num_prec)"
            fi
        else
            full_type="$data_type"
        fi

        # Get column comment
        COL_DESC=$(query_db "SELECT col_description('public.$table'::regclass, (SELECT ordinal_position FROM information_schema.columns WHERE table_schema = 'public' AND table_name = '$table' AND column_name = '$col_name'));")

        # Format nullable
        if [ "$nullable" = "YES" ]; then
            nullable_text="✓"
        else
            nullable_text="✗"
        fi

        # Format default
        if [ -z "$col_default" ] || [ "$col_default" = "" ]; then
            col_default="-"
        fi

        # Format description
        if [ -z "$COL_DESC" ] || [ "$COL_DESC" = "" ]; then
            COL_DESC="-"
        fi

        echo "| $col_name | $full_type | $nullable_text | $col_default | $COL_DESC |" >> "$OUTPUT_FILE"
    done <<< "$COLUMNS"

    echo "" >> "$OUTPUT_FILE"

    # Primary Key
    PK_INFO=$(query_db "
        SELECT a.attname
        FROM pg_index i
        JOIN pg_attribute a ON a.attrelid = i.indrelid AND a.attnum = ANY(i.indkey)
        WHERE i.indrelid = 'public.$table'::regclass AND i.indisprimary
        ORDER BY a.attnum;
    ")

    if [ ! -z "$PK_INFO" ] && [ "$PK_INFO" != "" ]; then
        echo "### Primary Key" >> "$OUTPUT_FILE"
        echo "" >> "$OUTPUT_FILE"
        echo "\`\`\`" >> "$OUTPUT_FILE"
        echo "$PK_INFO" | tr ',' '\n' >> "$OUTPUT_FILE"
        echo "\`\`\`" >> "$OUTPUT_FILE"
        echo "" >> "$OUTPUT_FILE"
    fi

    # Foreign Keys
    FK_INFO=$(query_db "
        SELECT
            tc.constraint_name,
            kcu.column_name,
            ccu.table_name AS foreign_table_name,
            ccu.column_name AS foreign_column_name,
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
            AND tc.table_name = '$table';
    ")

    if [ ! -z "$FK_INFO" ] && [ "$FK_INFO" != "" ]; then
        echo "### Foreign Keys" >> "$OUTPUT_FILE"
        echo "" >> "$OUTPUT_FILE"
        echo "| Constraint | Column | References | On Delete |" >> "$OUTPUT_FILE"
        echo "|------------|--------|------------|-----------|" >> "$OUTPUT_FILE"

        while IFS=',' read -r constraint_name column_name foreign_table foreign_column delete_rule; do
            echo "| $constraint_name | $column_name | $foreign_table($foreign_column) | $delete_rule |" >> "$OUTPUT_FILE"
        done <<< "$FK_INFO"

        echo "" >> "$OUTPUT_FILE"
    fi

    # Unique Constraints
    UNIQUE_INFO=$(query_db "
        SELECT
            tc.constraint_name,
            string_agg(kcu.column_name, ', ' ORDER BY kcu.ordinal_position)
        FROM information_schema.table_constraints tc
        JOIN information_schema.key_column_usage kcu
            ON tc.constraint_name = kcu.constraint_name
            AND tc.table_schema = kcu.table_schema
        WHERE tc.constraint_type = 'UNIQUE'
            AND tc.table_schema = 'public'
            AND tc.table_name = '$table'
        GROUP BY tc.constraint_name;
    ")

    if [ ! -z "$UNIQUE_INFO" ] && [ "$UNIQUE_INFO" != "" ]; then
        echo "### Unique Constraints" >> "$OUTPUT_FILE"
        echo "" >> "$OUTPUT_FILE"
        echo "| Constraint | Columns |" >> "$OUTPUT_FILE"
        echo "|------------|---------|" >> "$OUTPUT_FILE"

        while IFS=',' read -r constraint_name columns; do
            echo "| $constraint_name | $columns |" >> "$OUTPUT_FILE"
        done <<< "$UNIQUE_INFO"

        echo "" >> "$OUTPUT_FILE"
    fi

    # Check Constraints
    CHECK_INFO=$(query_db "
        SELECT
            con.conname,
            pg_get_constraintdef(con.oid)
        FROM pg_constraint con
        JOIN pg_class rel ON rel.oid = con.conrelid
        JOIN pg_namespace nsp ON nsp.oid = connamespace
        WHERE nsp.nspname = 'public'
            AND rel.relname = '$table'
            AND con.contype = 'c';
    ")

    if [ ! -z "$CHECK_INFO" ] && [ "$CHECK_INFO" != "" ]; then
        echo "### Check Constraints" >> "$OUTPUT_FILE"
        echo "" >> "$OUTPUT_FILE"
        echo "| Constraint | Definition |" >> "$OUTPUT_FILE"
        echo "|------------|------------|" >> "$OUTPUT_FILE"

        while IFS=',' read -r constraint_name definition; do
            echo "| $constraint_name | \`$definition\` |" >> "$OUTPUT_FILE"
        done <<< "$CHECK_INFO"

        echo "" >> "$OUTPUT_FILE"
    fi

    # Indexes
    INDEX_INFO=$(query_db "
        SELECT
            i.relname AS index_name,
            a.attname AS column_name,
            ix.indisunique AS is_unique,
            ix.indisprimary AS is_primary
        FROM pg_class t
        JOIN pg_index ix ON t.oid = ix.indrelid
        JOIN pg_class i ON i.oid = ix.indexrelid
        JOIN pg_attribute a ON a.attrelid = t.oid AND a.attnum = ANY(ix.indkey)
        WHERE t.relkind = 'r'
            AND t.relnamespace = (SELECT oid FROM pg_namespace WHERE nspname = 'public')
            AND t.relname = '$table'
            AND NOT ix.indisprimary
        ORDER BY i.relname, a.attnum;
    ")

    if [ ! -z "$INDEX_INFO" ] && [ "$INDEX_INFO" != "" ]; then
        echo "### Indexes" >> "$OUTPUT_FILE"
        echo "" >> "$OUTPUT_FILE"
        echo "| Index | Column | Unique |" >> "$OUTPUT_FILE"
        echo "|-------|--------|--------|" >> "$OUTPUT_FILE"

        while IFS=',' read -r index_name column_name is_unique is_primary; do
            if [ "$is_unique" = "t" ]; then
                unique_text="✓"
            else
                unique_text="✗"
            fi
            echo "| $index_name | $column_name | $unique_text |" >> "$OUTPUT_FILE"
        done <<< "$INDEX_INFO"

        echo "" >> "$OUTPUT_FILE"
    fi

    echo "---" >> "$OUTPUT_FILE"
    echo "" >> "$OUTPUT_FILE"
done

# Database statistics
echo "## Database Statistics" >> "$OUTPUT_FILE"
echo "" >> "$OUTPUT_FILE"
echo "| Metric | Value |" >> "$OUTPUT_FILE"
echo "|--------|-------|" >> "$OUTPUT_FILE"

TOTAL_TABLES=$(echo "$TABLES" | wc -l)
echo "| Total Tables | $TOTAL_TABLES |" >> "$OUTPUT_FILE"

TOTAL_COLUMNS=$(query_db "SELECT COUNT(*) FROM information_schema.columns WHERE table_schema = 'public';")
echo "| Total Columns | $TOTAL_COLUMNS |" >> "$OUTPUT_FILE"

TOTAL_INDEXES=$(query_db "SELECT COUNT(*) FROM pg_indexes WHERE schemaname = 'public';")
echo "| Total Indexes | $TOTAL_INDEXES |" >> "$OUTPUT_FILE"

TOTAL_CONSTRAINTS=$(query_db "SELECT COUNT(*) FROM information_schema.table_constraints WHERE table_schema = 'public';")
echo "| Total Constraints | $TOTAL_CONSTRAINTS |" >> "$OUTPUT_FILE"

echo "" >> "$OUTPUT_FILE"
echo "---" >> "$OUTPUT_FILE"
echo "" >> "$OUTPUT_FILE"
echo "*Documentation generated automatically by CI/CD pipeline*" >> "$OUTPUT_FILE"

echo "✅ Schema documentation generated successfully: $OUTPUT_FILE"
