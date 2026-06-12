-- liquibase formatted sql

--changeset admin:create-extensions
CREATE EXTENSION IF NOT EXISTS pgcrypto;
--rollback DROP EXTENSION pgcrypto;

--changeset admin:create-uuid-v7-function
CREATE OR REPLACE FUNCTION gen_random_uuid_v7()
RETURNS UUID AS '
DECLARE
    ts_ms BIGINT := EXTRACT(EPOCH FROM NOW()) * 1000;
    uuid_bytes BYTEA;
BEGIN
    -- Собираем 16 байт:
    -- 1. 6 байт времени (Big Endian)
    -- 2. 10 байт случайных данных
    uuid_bytes := decode(lpad(to_hex(ts_ms), 12, ''0''), ''hex'') || gen_random_bytes(10);

    -- Устанавливаем версию UUID (7) и variant (RFC 4122)
    -- Байт 6: биты 4-7 = 0111 (версия 7)
    uuid_bytes := set_byte(uuid_bytes, 6, (get_byte(uuid_bytes, 6) & x''0F''::int) | x''70''::int);
    -- Байт 8: биты 6-7 = 10 (variant RFC 4122)
    uuid_bytes := set_byte(uuid_bytes, 8, (get_byte(uuid_bytes, 8) & x''3F''::int) | x''80''::int);

    RETURN encode(uuid_bytes, ''hex'')::UUID;
END;
' LANGUAGE plpgsql VOLATILE;
--rollback DROP FUNCTION gen_random_uuid_v7();

-- changeset admin:create-roles-enum
CREATE TYPE role_enum AS ENUM ('ADMIN', 'OWNER', 'WORKER');
-- rollback DROP TYPE role_enum;

--changeset admin:create-users-table
CREATE TABLE users
(
    id         UUID        DEFAULT gen_random_uuid_v7() PRIMARY KEY,
    username   VARCHAR(255)                          NOT NULL UNIQUE,
    password   VARCHAR(255)                          NOT NULL,
    role       role_enum                             NOT NULL,
    created_at TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP NOT NULL,
    updated_at TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP NOT NULL
);
--rollback DROP TABLE users;

--changeset admin:create-clients-table
CREATE TABLE clients
(
    id          UUID        DEFAULT gen_random_uuid_v7() PRIMARY KEY,
    full_name   VARCHAR(500)                          NOT NULL,
    birth_date  DATE,
    birth_place VARCHAR(500),
    inn         VARCHAR(12),
    snils       VARCHAR(14),
    address     TEXT,
    created_at  TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP NOT NULL,
    updated_at  TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP NOT NULL
);
--rollback DROP TABLE clients;

--changeset admin:create-documents-table
CREATE TABLE documents
(
    id            UUID        DEFAULT gen_random_uuid_v7() PRIMARY KEY,
    user_id       UUID                                  NOT NULL REFERENCES users (id) ON DELETE CASCADE,
    client_id     UUID                                  NOT NULL REFERENCES clients (id) ON DELETE CASCADE,
    template_type VARCHAR(50)                           NOT NULL,
    metadata      JSONB                                 NOT NULL,
    content       BYTEA                                 NOT NULL,
    size_kb       INTEGER,
    created_at    TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP NOT NULL,
    updated_at    TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP NOT NULL
);
--rollback DROP TABLE documents;

--changeset admin:add-triggers
CREATE OR REPLACE FUNCTION update_updated_at_column()
    RETURNS TRIGGER AS '
    BEGIN
        NEW.updated_at = CURRENT_TIMESTAMP;
        RETURN NEW;
    END;
' LANGUAGE plpgsql;

CREATE TRIGGER update_users_updated_at
    BEFORE UPDATE
    ON users
    FOR EACH ROW
EXECUTE FUNCTION update_updated_at_column();

CREATE TRIGGER update_clients_updated_at
    BEFORE UPDATE
    ON clients
    FOR EACH ROW
EXECUTE FUNCTION update_updated_at_column();

CREATE TRIGGER update_documents_updated_at
    BEFORE UPDATE
    ON documents
    FOR EACH ROW
EXECUTE FUNCTION update_updated_at_column();
--rollback DROP TRIGGER IF EXISTS update_users_updated_at ON users; DROP TRIGGER IF EXISTS update_clients_updated_at ON clients; DROP TRIGGER IF EXISTS update_documents_updated_at ON documents; DROP FUNCTION IF EXISTS update_updated_at_column();


--changeset admin:create-owner-table
CREATE TABLE owners (
                        id UUID DEFAULT gen_random_uuid_v7() PRIMARY KEY,
                        user_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
                        full_name VARCHAR(500) NOT NULL,
                        full_name_short VARCHAR(50),
                        mail_address TEXT,
                        email VARCHAR(255),
                        sro_name VARCHAR(500),
                        sro_inn VARCHAR(12),
                        sro_ogrn VARCHAR(15),
                        sro_address TEXT,
                        user_inn VARCHAR(12),
                        user_snils VARCHAR(14),
                        created_at TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP NOT NULL,
                        updated_at TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP NOT NULL
);
--rollback DROP TABLE owners;

--changeset admin:add-triggers-to-owner
CREATE TRIGGER update_owners_updated_at
    BEFORE UPDATE ON owners
    FOR EACH ROW
EXECUTE FUNCTION update_updated_at_column();
--rollback DROP TRIGGER IF EXISTS update_owners_updated_at ON owners;

--changeset admin:create-staffs-table
CREATE TABLE staffs (
                        id UUID DEFAULT gen_random_uuid_v7() PRIMARY KEY,
                        user_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
                        full_name VARCHAR(500) NOT NULL,
                        full_name_short VARCHAR(50),
                        email VARCHAR(255),
                        user_inn VARCHAR(12),
                        user_snils VARCHAR(14),
                        owner_id UUID NOT NULL REFERENCES owners(id) ON DELETE CASCADE,
                        created_at TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP NOT NULL,
                        updated_at TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP NOT NULL
);
--rollback DROP TABLE staffs;

--changeset admin:add-triggers-to-staffs
CREATE TRIGGER update_staffs_updated_at
    BEFORE UPDATE ON staffs
    FOR EACH ROW
EXECUTE FUNCTION update_updated_at_column();
--rollback DROP TRIGGER IF EXISTS update_staffs_updated_at ON staffs;

--changeset admin:update-clients-table-v01
alter TABLE clients
add column full_name_short varchar(256);

--changeset admin:update-constraint-inn-clients-table-v01
ALTER TABLE clients
    ADD CONSTRAINT unique_clients_inn UNIQUE (inn);