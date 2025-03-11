CREATE TABLE budget_items (
                              id BIGSERIAL PRIMARY KEY,
                              name VARCHAR(255) NOT NULL,
                              allocated_amount DECIMAL(19,2) NOT NULL,
                              status VARCHAR(50) NOT NULL,
                              budget_id BIGINT,
                              created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                              updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                              CONSTRAINT fk_budget FOREIGN KEY (budget_id) REFERENCES budgets(id) ON DELETE SET NULL
);

-- Ensure updated_at is automatically updated when the row is modified
CREATE FUNCTION set_updated_at_budget_items() RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = NOW();
RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER trigger_set_updated_at_budget_items
    BEFORE UPDATE ON budget_items
    FOR EACH ROW
    EXECUTE FUNCTION set_updated_at_budget_items();
