-- Remove unit_cost column from ingredients table
-- This field should be calculated dynamically by the domain logic, not stored in database
ALTER TABLE ingredients DROP COLUMN unit_cost;

-- Update table comment to reflect the change
COMMENT ON TABLE ingredients IS 'Stores ingredient information including package details. Unit costs are calculated dynamically by domain logic.';