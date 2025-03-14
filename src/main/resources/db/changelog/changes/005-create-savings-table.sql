CREATE TABLE savings (
                         id BIGSERIAL PRIMARY KEY,
                         goal_name VARCHAR(255) NOT NULL,
                         target_amount DECIMAL(19,2) NOT NULL,
                         saved_amount DECIMAL(19,2) NOT NULL DEFAULT 0.00,
                         start_date DATE NOT NULL,
                         end_date DATE NOT NULL,
                         status VARCHAR(50),
                         user_id BIGINT,
                         created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                         updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                         CONSTRAINT fk_savings_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE SET NULL
);

-- Ensure updated_at is automatically updated when the row is modified
CREATE FUNCTION set_updated_at_savings() RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = NOW();
RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER trigger_set_updated_at_savings
    BEFORE UPDATE ON savings
    FOR EACH ROW
    EXECUTE FUNCTION set_updated_at_savings();
