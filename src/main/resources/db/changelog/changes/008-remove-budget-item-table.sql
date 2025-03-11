-- Step 1: Drop foreign key constraints referencing budget_item
ALTER TABLE expenses DROP CONSTRAINT IF EXISTS fk_expenses_budget_items;
ALTER TABLE budget_item DROP CONSTRAINT IF EXISTS budget_item_buget_id_fkey;

-- Step 2: Remove the `budget_item` table
DROP TABLE IF EXISTS budget_item CASCADE;

-- Step 3: Ensure `expenses` links to `budget_items`
ALTER TABLE expenses ADD COLUMN IF NOT EXISTS budget_items_id BIGINT;

-- Step 4: Create the correct foreign key linking `expenses` to `budget_items`
ALTER TABLE expenses
    ADD CONSTRAINT fk_expenses_budget_items
        FOREIGN KEY (budget_items_id)
            REFERENCES budget_items(id)
            ON DELETE SET NULL;
