ALTER TABLE expenses
DROP CONSTRAINT IF EXISTS fk_expenses_budget_item,
ADD CONSTRAINT fk_expenses_budget_item
FOREIGN KEY (budget_items_id)
REFERENCES budget_items(id)
ON DELETE CASCADE;
