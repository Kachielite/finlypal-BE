-- Step 1: Alter expenses table to add savings_items_id column
ALTER TABLE expenses
    ADD COLUMN savings_items_id BIGINT;

-- Step 2: Add foreign key constraint to link expenses to savings_item
ALTER TABLE expenses
    ADD CONSTRAINT fk_expenses_savings_item
        FOREIGN KEY (savings_items_id)
            REFERENCES savings_item (id)
            ON DELETE SET NULL;
