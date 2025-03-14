ALTER TABLE budget_items
DROP CONSTRAINT IF EXISTS fk_budget_items_budget,
ADD CONSTRAINT fk_budget_items_budget
FOREIGN KEY (budget_id)
REFERENCES budgets(id)
ON DELETE CASCADE;
