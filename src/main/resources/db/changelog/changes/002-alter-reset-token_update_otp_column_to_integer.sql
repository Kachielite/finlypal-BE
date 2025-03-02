-- Change column type to INTEGER with a CHECK constraint
ALTER TABLE reset_tokens
ALTER COLUMN otp TYPE INTEGER
    USING otp::integer,
    ADD CONSTRAINT otp_4_digits CHECK (otp BETWEEN 1000 AND 9999);
