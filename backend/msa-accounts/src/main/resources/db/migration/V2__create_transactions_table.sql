-- V2__create_transactions_table.sql
-- resources/db/migration/V2__create_transactions_table.sql

CREATE TABLE IF NOT EXISTS transactions (
    id SERIAL PRIMARY KEY,
    account_id INTEGER NOT NULL,
    transaction_number VARCHAR(20) NOT NULL UNIQUE,
    type VARCHAR(20) NOT NULL,
    amount DECIMAL(19, 2) NOT NULL,
    currency VARCHAR(3) NOT NULL,
    status VARCHAR(20) NOT NULL,
    description VARCHAR(500),
    reference_number VARCHAR(50),
    related_account_id UUID,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    processed_at TIMESTAMP,
    
    CONSTRAINT fk_account FOREIGN KEY (account_id) REFERENCES accounts(id) ON DELETE CASCADE,
    CONSTRAINT chk_transaction_type CHECK (type IN ('DEPOSIT', 'WITHDRAWAL', 'TRANSFER_IN', 'TRANSFER_OUT', 'FEE', 'INTEREST', 'REVERSAL')),
    CONSTRAINT chk_transaction_status CHECK (status IN ('PENDING', 'PROCESSING', 'COMPLETED', 'FAILED', 'REVERSED')),
    CONSTRAINT chk_amount_positive CHECK (amount > 0)
);

-- Indexes
CREATE INDEX IF NOT EXISTS idx_transaction_number ON transactions(transaction_number);
CREATE INDEX IF NOT EXISTS idx_account_id ON transactions(account_id);
CREATE INDEX IF NOT EXISTS idx_status ON transactions(status);
CREATE INDEX IF NOT EXISTS idx_created_at ON transactions(created_at DESC);
CREATE INDEX IF NOT EXISTS idx_type ON transactions(type);