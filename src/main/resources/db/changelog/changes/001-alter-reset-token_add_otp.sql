-- Add otp column to ResetToken table
ALTER TABLE ResetToken
    ADD COLUMN otp VARCHAR(255);
