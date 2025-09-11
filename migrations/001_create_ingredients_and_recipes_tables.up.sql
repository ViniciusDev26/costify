-- Create ingredients table
CREATE TABLE ingredients (
  id VARCHAR(255) PRIMARY KEY,
  name VARCHAR(255) NOT NULL UNIQUE,
  package_quantity DECIMAL(10,3) NOT NULL CHECK (package_quantity > 0),
  package_price DECIMAL(10,2) NOT NULL CHECK (package_price >= 0),
  package_unit VARCHAR(50) NOT NULL,
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Create recipes table
CREATE TABLE recipes (
  id VARCHAR(255) PRIMARY KEY,
  name VARCHAR(255) NOT NULL UNIQUE,
  total_cost DECIMAL(10,2) NOT NULL DEFAULT 0 CHECK (total_cost >= 0),
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Create recipe_ingredients junction table
CREATE TABLE recipe_ingredients (
  id SERIAL PRIMARY KEY,
  recipe_id VARCHAR(255) NOT NULL,
  ingredient_id VARCHAR(255) NOT NULL,
  quantity DECIMAL(10,3) NOT NULL CHECK (quantity > 0),
  unit VARCHAR(50) NOT NULL,
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  
  -- Foreign key constraints
  CONSTRAINT fk_recipe_ingredients_recipe 
    FOREIGN KEY (recipe_id) REFERENCES recipes(id) ON DELETE CASCADE,
  CONSTRAINT fk_recipe_ingredients_ingredient 
    FOREIGN KEY (ingredient_id) REFERENCES ingredients(id) ON DELETE CASCADE,
    
  -- Ensure unique recipe-ingredient combinations
  CONSTRAINT uk_recipe_ingredient UNIQUE (recipe_id, ingredient_id)
);

-- Create indexes for better query performance
CREATE INDEX idx_ingredients_name ON ingredients(name);
CREATE INDEX idx_recipes_name ON recipes(name);
CREATE INDEX idx_recipe_ingredients_recipe_id ON recipe_ingredients(recipe_id);
CREATE INDEX idx_recipe_ingredients_ingredient_id ON recipe_ingredients(ingredient_id);

-- Add comments for documentation
COMMENT ON TABLE ingredients IS 'Stores ingredient information including package details and unit costs';
COMMENT ON TABLE recipes IS 'Stores recipe information';  
COMMENT ON TABLE recipe_ingredients IS 'Junction table linking recipes to ingredients with quantities';

COMMENT ON COLUMN ingredients.package_quantity IS 'Total quantity in the package';
COMMENT ON COLUMN ingredients.package_price IS 'Price of the entire package';
COMMENT ON COLUMN ingredients.package_unit IS 'Unit of measurement for the package (ML, L, TBSP, G, KG, TBSP_BUTTER, UN)';

COMMENT ON COLUMN recipe_ingredients.quantity IS 'Quantity of ingredient needed for this recipe';
COMMENT ON COLUMN recipe_ingredients.unit IS 'Unit of measurement for this recipe ingredient';