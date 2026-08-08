-- V3__create_audit_trigger.sql
-- resources/db/migration/V3__create_audit_trigger.sql

-- Audit log table
CREATE TABLE IF NOT EXISTS account_audit_log (
    audit_id BIGSERIAL PRIMARY KEY,
    account_id INTEGER NOT NULL,
    operation VARCHAR(10) NOT NULL,
    changed_by VARCHAR(100),
    changed_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    old_values JSONB,
    new_values JSONB
);

CREATE INDEX idx_account_audit_account_id ON account_audit_log(account_id);
CREATE INDEX idx_account_audit_changed_at ON account_audit_log(changed_at DESC);

-- Trigger function
CREATE OR REPLACE FUNCTION audit_account_changes()
RETURNS TRIGGER AS $$
BEGIN
    IF (TG_OP = 'DELETE') THEN
        INSERT INTO account_audit_log (account_id, operation, old_values)
        VALUES (OLD.id, 'DELETE', row_to_json(OLD));
        RETURN OLD;
    ELSIF (TG_OP = 'UPDATE') THEN
        INSERT INTO account_audit_log (account_id, operation, old_values, new_values)
        VALUES (NEW.id, 'UPDATE', row_to_json(OLD), row_to_json(NEW));
        RETURN NEW;
    ELSIF (TG_OP = 'INSERT') THEN
        INSERT INTO account_audit_log (account_id, operation, new_values)
        VALUES (NEW.id, 'INSERT', row_to_json(NEW));
        RETURN NEW;
    END IF;
    RETURN NULL;
END;
$$ LANGUAGE plpgsql;

-- Attach trigger
CREATE TRIGGER account_audit_trigger
AFTER INSERT OR UPDATE OR DELETE ON accounts
FOR EACH ROW EXECUTE FUNCTION audit_account_changes();