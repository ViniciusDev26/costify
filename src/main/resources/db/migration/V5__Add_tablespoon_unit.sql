-- Add TBSP (tablespoon) units to the measurement_unit ENUM
-- This is safe to run on existing databases
ALTER TYPE measurement_unit ADD VALUE 'TBSP';
ALTER TYPE measurement_unit ADD VALUE 'TBSP_BUTTER';

-- Update comment to include the new units
COMMENT ON TYPE measurement_unit IS 'Measurement units: ML (milliliter), L (liter), TBSP (tablespoon volume = 15ml), G (gram), KG (kilogram), TBSP_BUTTER (tablespoon butter weight ≈ 14g), UN (unit/piece)';