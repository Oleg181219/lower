-- liquibase formatted sql

-- changeset admin:add-court-fields-to-clients
ALTER TABLE clients ADD COLUMN court_name VARCHAR(500);
ALTER TABLE clients ADD COLUMN case_number VARCHAR(100);
ALTER TABLE clients ADD COLUMN court_decision_date DATE;
ALTER TABLE clients ADD COLUMN procedure_type VARCHAR(100);
--rollback ALTER TABLE clients DROP COLUMN court_name, DROP COLUMN case_number, DROP COLUMN court_decision_date, DROP COLUMN procedure_type;

-- changeset admin:add-owner-to-clients
ALTER TABLE clients ADD COLUMN owner_id UUID REFERENCES owners(id);
--rollback ALTER TABLE clients DROP COLUMN owner_id;

-- changeset admin:create-recipient-orgs-table
CREATE TABLE recipient_orgs (
                                id UUID DEFAULT gen_random_uuid_v7() PRIMARY KEY,
                                doc_type VARCHAR(50) NOT NULL UNIQUE,
                                org_name VARCHAR(500) NOT NULL,
                                org_address TEXT NOT NULL,
                                org_note TEXT
);
--rollback DROP TABLE recipient_orgs;
