-- Add otp column to ResetToken table
ALTER TABLE reset_tokens
    ADD COLUMN otp VARCHAR(255);
