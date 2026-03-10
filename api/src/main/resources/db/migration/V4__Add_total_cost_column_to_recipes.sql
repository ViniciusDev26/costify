-- Add total_cost column to recipes table
ALTER TABLE recipes 
ADD COLUMN total_cost DECIMAL(10,2) NOT NULL DEFAULT 0.00 CHECK (total_cost >= 0);

-- Add comment for documentation
COMMENT ON COLUMN recipes.total_cost IS 'Total calculated cost of the recipe based on ingredient costs and quantities';