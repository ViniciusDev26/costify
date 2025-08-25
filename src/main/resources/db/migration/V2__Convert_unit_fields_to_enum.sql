-- Create ENUM type for measurement units
CREATE TYPE measurement_unit AS ENUM ('ML', 'L', 'G', 'KG', 'UN');

-- Add temporary columns with ENUM type
ALTER TABLE ingredients 
ADD COLUMN package_unit_enum measurement_unit;

ALTER TABLE recipe_ingredients 
ADD COLUMN unit_enum measurement_unit;

-- Copy data from VARCHAR columns to ENUM columns
-- This will fail if there are any invalid values, ensuring data integrity
UPDATE ingredients 
SET package_unit_enum = package_unit::measurement_unit;

UPDATE recipe_ingredients 
SET unit_enum = unit::measurement_unit;

-- Drop old VARCHAR columns
ALTER TABLE ingredients 
DROP COLUMN package_unit;

ALTER TABLE recipe_ingredients 
DROP COLUMN unit;

-- Rename ENUM columns to original names
ALTER TABLE ingredients 
RENAME COLUMN package_unit_enum TO package_unit;

ALTER TABLE recipe_ingredients 
RENAME COLUMN unit_enum TO unit;

-- Add NOT NULL constraints to the new ENUM columns
ALTER TABLE ingredients 
ALTER COLUMN package_unit SET NOT NULL;

ALTER TABLE recipe_ingredients 
ALTER COLUMN unit SET NOT NULL;

-- Add comments for the new ENUM type and columns
COMMENT ON TYPE measurement_unit IS 'Measurement units: ML (milliliter), L (liter), G (gram), KG (kilogram), UN (unit/piece)';

COMMENT ON COLUMN ingredients.package_unit IS 'Unit of measurement for the package (ENUM: ML, L, G, KG, UN)';
COMMENT ON COLUMN recipe_ingredients.unit IS 'Unit of measurement for this recipe ingredient (ENUM: ML, L, G, KG, UN)';

-- Create indexes on the new ENUM columns for better query performance
CREATE INDEX idx_ingredients_package_unit ON ingredients(package_unit);
CREATE INDEX idx_recipe_ingredients_unit ON recipe_ingredients(unit);