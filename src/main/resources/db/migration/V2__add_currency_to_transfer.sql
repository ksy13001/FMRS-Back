-- Adds currency code for transfer fees
ALTER TABLE transfer
    ADD COLUMN currency VARCHAR(8) NULL;
