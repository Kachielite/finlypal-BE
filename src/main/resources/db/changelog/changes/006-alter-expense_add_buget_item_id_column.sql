-- Step 1: Add budget_item_id column to expenses table
ALTER TABLE expenses
    ADD COLUMN budget_item_id BIGINT;

-- Step 2: Ensure existing records have budget_item_id set to NULL (not needed explicitly, but included for clarity)
UPDATE expenses
SET budget_item_id = NULL;

-- Step 3: Add foreign key constraint linking to budget_items table
ALTER TABLE expenses
    ADD CONSTRAINT fk_expenses_budget_items
        FOREIGN KEY (budget_item_id)
            REFERENCES budget_items (id)
            ON DELETE SET NULL;
