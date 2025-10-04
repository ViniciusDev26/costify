# Database Schema Documentation

**Generated:** 2025-10-04 00:14:32 UTC

**Database:** costify

**PostgreSQL Version:** PostgreSQL 16.9 on x86_64-pc-linux-musl, compiled by gcc (Alpine 14.2.0) 14.2.0, 64-bit

## Table of Contents

- [flyway_schema_history](#flyway_schema_history)
- [ingredients](#ingredients)
- [recipe_ingredients](#recipe_ingredients)
- [recipes](#recipes)

---

## flyway_schema_history

### Columns

| Column | Type | Nullable | Default | Description |
|--------|------|----------|---------|-------------|
| installed_rank | integer(32) | ✗ | - | - |
| version | character varying(50) | ✓ | - | - |
| description | character varying(200) | ✗ | - | - |
| type | character varying(20) | ✗ | - | - |
| script | character varying(1000) | ✗ | - | - |
| checksum | integer(32) | ✓ | - | - |
| installed_by | character varying(100) | ✗ | - | - |
| installed_on | timestamp without time zone | ✗ | now() | - |
| execution_time | integer(32) | ✗ | - | - |
| success | boolean | ✗ | - | - |

### Primary Key

```
installed_rank
```

### Indexes

| Index | Column | Unique |
|-------|--------|--------|
| flyway_schema_history_s_idx | success | ✗ |

---

## ingredients

**Description:** Stores ingredient information including package details. Unit costs are calculated dynamically by domain logic.

### Columns

| Column | Type | Nullable | Default | Description |
|--------|------|----------|---------|-------------|
| id | character varying(255) | ✗ | - | - |
| name | character varying(255) | ✗ | - | - |
| package_quantity | numeric(10,3) | ✗ | - | Total quantity in the package |
| package_price | numeric(10,2) | ✗ | - | Price of the entire package |
| created_at | timestamp without time zone | ✓ | CURRENT_TIMESTAMP | - |
| updated_at | timestamp without time zone | ✓ | CURRENT_TIMESTAMP | - |
| package_unit | USER-DEFINED | ✗ | - | Unit of measurement for the package (ENUM: ML, L, G, KG, UN) |

### Primary Key

```
id
```

### Unique Constraints

| Constraint | Columns |
|------------|---------|
| ingredients_name_key | name |

### Check Constraints

| Constraint | Definition |
|------------|------------|
| ingredients_package_quantity_check | `CHECK ((package_quantity > (0)::numeric))` |
| ingredients_package_price_check | `CHECK ((package_price >= (0)::numeric))` |

### Indexes

| Index | Column | Unique |
|-------|--------|--------|
| idx_ingredients_name | name | ✗ |
| idx_ingredients_package_unit | package_unit | ✗ |
| ingredients_name_key | name | ✓ |

---

## recipe_ingredients

**Description:** Junction table linking recipes to ingredients with quantities

### Columns

| Column | Type | Nullable | Default | Description |
|--------|------|----------|---------|-------------|
| id | integer(32) | ✗ | nextval('recipe_ingredients_id_seq'::regclass) | - |
| recipe_id | character varying(255) | ✗ | - | - |
| ingredient_id | character varying(255) | ✗ | - | - |
| quantity | numeric(10,3) | ✗ | - | Quantity of ingredient needed for this recipe |
| created_at | timestamp without time zone | ✓ | CURRENT_TIMESTAMP | - |
| unit | USER-DEFINED | ✗ | - | Unit of measurement for this recipe ingredient (ENUM: ML, L, G, KG, UN) |

### Primary Key

```
id
```

### Foreign Keys

| Constraint | Column | References | On Delete |
|------------|--------|------------|-----------|
| fk_recipe_ingredients_recipe | recipe_id | recipes(id) | CASCADE |
| fk_recipe_ingredients_ingredient | ingredient_id | ingredients(id) | CASCADE |

### Unique Constraints

| Constraint | Columns |
|------------|---------|
| uk_recipe_ingredient | recipe_id, ingredient_id |

### Check Constraints

| Constraint | Definition |
|------------|------------|
| recipe_ingredients_quantity_check | `CHECK ((quantity > (0)::numeric))` |

### Indexes

| Index | Column | Unique |
|-------|--------|--------|
| idx_recipe_ingredients_ingredient_id | ingredient_id | ✗ |
| idx_recipe_ingredients_recipe_id | recipe_id | ✗ |
| idx_recipe_ingredients_unit | unit | ✗ |
| uk_recipe_ingredient | recipe_id | ✓ |
| uk_recipe_ingredient | ingredient_id | ✓ |

---

## recipes

**Description:** Stores recipe information

### Columns

| Column | Type | Nullable | Default | Description |
|--------|------|----------|---------|-------------|
| id | character varying(255) | ✗ | - | - |
| name | character varying(255) | ✗ | - | - |
| created_at | timestamp without time zone | ✓ | CURRENT_TIMESTAMP | - |
| updated_at | timestamp without time zone | ✓ | CURRENT_TIMESTAMP | - |
| total_cost | numeric(10,2) | ✗ | 0.00 | Total calculated cost of the recipe based on ingredient costs and quantities |

### Primary Key

```
id
```

### Unique Constraints

| Constraint | Columns |
|------------|---------|
| recipes_name_key | name |

### Check Constraints

| Constraint | Definition |
|------------|------------|
| recipes_total_cost_check | `CHECK ((total_cost >= (0)::numeric))` |

### Indexes

| Index | Column | Unique |
|-------|--------|--------|
| idx_recipes_name | name | ✗ |
| recipes_name_key | name | ✓ |

---

## Database Statistics

| Metric | Value |
|--------|-------|
| Total Tables | 4 |
| Total Columns | 28 |
| Total Indexes | 14 |
| Total Constraints | 34 |

---

*Documentation generated automatically by CI/CD pipeline*
