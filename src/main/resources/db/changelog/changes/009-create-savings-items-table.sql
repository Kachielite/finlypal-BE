-- Step 1: Create savings_item table
CREATE TABLE savings_item (
                              id BIGSERIAL PRIMARY KEY,
                              name VARCHAR(255) NOT NULL,
                              allocated_amount DECIMAL(10,2) NOT NULL DEFAULT 0.00,
                              status VARCHAR(50) CHECK (status IN ('ON_TRACK', 'BEHIND', 'COMPLETED')),
                              savings_id BIGINT NOT NULL,
                              created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                              updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                              CONSTRAINT fk_savings FOREIGN KEY (savings_id) REFERENCES savings(id) ON DELETE CASCADE
);

-- Step 2: Create trigger function to update updated_at on row modification
CREATE FUNCTION update_timestamp()
    RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = CURRENT_TIMESTAMP;
RETURN NEW;
END;
$$ LANGUAGE plpgsql;

-- Step 3: Create trigger for savings_item table
CREATE TRIGGER trigger_update_timestamp
    BEFORE UPDATE ON savings_item
    FOR EACH ROW
    EXECUTE FUNCTION update_timestamp();
