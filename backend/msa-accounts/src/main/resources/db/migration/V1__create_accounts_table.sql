CREATE TABLE IF NOT EXISTS accounts (
    id SERIAL PRIMARY KEY,
    client_id UUID NOT NULL,
    account_number VARCHAR(20) NOT NULL UNIQUE,
    type VARCHAR(20) NOT NULL,
    status VARCHAR(20) NOT NULL,
    balance DECIMAL(19, 2) NOT NULL DEFAULT 0.00,
    overdraft_limit DECIMAL(19, 2) DEFAULT 0.00,
    currency VARCHAR(3) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    
    CONSTRAINT chk_account_type CHECK (type IN ('SAVINGS', 'CHECKING', 'CREDIT', 'INVESTMENT')),
    CONSTRAINT chk_account_status CHECK (status IN ('ACTIVE', 'INACTIVE', 'BLOCKED', 'CLOSED')),
    CONSTRAINT chk_balance CHECK (balance >= -overdraft_limit)
);

CREATE SEQUENCE accounts_seq START 1 INCREMENT 1;

-- Indexes for performance
CREATE INDEX idx_account_number ON accounts(account_number);
CREATE INDEX idx_client_id ON accounts(client_id);
CREATE INDEX idx_status ON accounts(status);
CREATE INDEX idx_created_at ON accounts(created_at DESC);