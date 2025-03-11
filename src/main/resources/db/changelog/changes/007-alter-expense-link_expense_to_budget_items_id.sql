-- Step 1: Drop the old foreign key constraint (if it exists)
ALTER TABLE expenses
DROP CONSTRAINT IF EXISTS fk_expenses_budget_items;

-- Step 2: Drop the incorrect column (budget_item_id) if it exists
ALTER TABLE expenses
DROP COLUMN IF EXISTS budget_item_id;

-- Step 3: Add the correct column (budget_items_id)
ALTER TABLE expenses
    ADD COLUMN budget_items_id BIGINT;

-- Step 4: Add the correct foreign key constraint linking to budget_item
ALTER TABLE expenses
    ADD CONSTRAINT fk_expenses_budget_items
        FOREIGN KEY (budget_items_id)
            REFERENCES budget_item(id)
            ON DELETE SET NULL;

-- Step 5: Drop the budget_item table (if you no longer need it)
DROP TABLE IF EXISTS budget_item CASCADE;
