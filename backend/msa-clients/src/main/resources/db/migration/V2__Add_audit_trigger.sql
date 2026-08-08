CREATE TABLE IF NOT EXISTS client_audit_log (
    audit_id BIGSERIAL PRIMARY KEY,
    client_id UUID NOT NULL,
    operation VARCHAR(10) NOT NULL,
    changed_by VARCHAR(100),
    changed_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    old_values JSONB,
    new_values JSONB
);

CREATE INDEX idx_client_audit_client_id ON client_audit_log(client_id);
CREATE INDEX idx_client_audit_changed_at ON client_audit_log(changed_at DESC);

CREATE OR REPLACE FUNCTION audit_client_changes()
RETURNS TRIGGER AS $$
BEGIN
    IF (TG_OP = 'DELETE') THEN
        INSERT INTO client_audit_log (client_id, operation, old_values)
        VALUES (OLD.client_id, 'DELETE', row_to_json(OLD));
        RETURN OLD;
    ELSIF (TG_OP = 'UPDATE') THEN
        INSERT INTO client_audit_log (client_id, operation, old_values, new_values)
        VALUES (NEW.client_id, 'UPDATE', row_to_json(OLD), row_to_json(NEW));
        RETURN NEW;
    ELSIF (TG_OP = 'INSERT') THEN
        INSERT INTO client_audit_log (client_id, operation, new_values)
        VALUES (NEW.client_id, 'INSERT', row_to_json(NEW));
        RETURN NEW;
    END IF;
    RETURN NULL;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER client_audit_trigger
AFTER INSERT OR UPDATE OR DELETE ON clients
FOR EACH ROW EXECUTE FUNCTION audit_client_changes();