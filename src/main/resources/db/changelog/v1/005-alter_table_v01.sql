-- liquibase formatted sql

-- changeset admin:create-regions-enum
CREATE TYPE region_enum AS ENUM (
    '1', '2', '3', '4', '5', '6', '7', '8', '9', '10',
    '11', '12', '13', '14', '15', '16', '17', '18', '19', '20',
    '21', '22', '23', '24', '25', '26', '27', '28', '29', '30',
    '31', '32', '33', '34', '35', '36', '37', '38', '39', '40',
    '41', '42', '43', '44', '45', '46', '47', '48', '49', '50',
    '51', '52', '53', '54', '55', '56', '57', '58', '59', '60',
    '61', '62', '63', '64', '65', '66', '67', '68', '69', '70',
    '71', '72', '73', '74', '75', '76', '77', '78',
    '79', '83', '86', '87', '89', '90', '91', '92', '93', '94',
    '95', '99'
    );
-- rollback DROP TYPE region_enum;

--changeset admin:add-region-client-table
ALTER TABLE clients
    ADD COLUMN region region_enum;

--changeset admin:add-court_decisions-table
CREATE TABLE IF NOT EXISTS court_decisions
(
    id            UUID                     DEFAULT gen_random_uuid_v7() PRIMARY KEY,
    owner_id      UUID         NOT NULL,
    client_id     UUID         NOT NULL,
    court_name    VARCHAR(255) NOT NULL,
    decision_date DATE         NOT NULL,
    case_number   VARCHAR(100) NOT NULL,
    created_at    TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at    TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);


--changeset admin:add-idx_court_decisions_client_case_number
CREATE UNIQUE INDEX idx_court_decisions_client_case_number
    ON court_decisions (client_id, case_number);

--changeset admin:add-court_decisions-table-comment
COMMENT ON TABLE court_decisions IS 'Таблица судебных решений по делам о банкротстве';
COMMENT ON COLUMN court_decisions.id IS 'Уникальный идентификатор записи';
COMMENT ON COLUMN court_decisions.owner_id IS 'ID финансового управляющего (владельца дела)';
COMMENT ON COLUMN court_decisions.client_id IS 'ID клиента (должника)';
COMMENT ON COLUMN court_decisions.court_name IS 'Наименование суда в родительном падеже (например: "Арбитражного суда Ростовской области")';
COMMENT ON COLUMN court_decisions.decision_date IS 'Дата вынесения решения о признании банкротом';
COMMENT ON COLUMN court_decisions.case_number IS 'Номер дела о банкротстве (например: "А53-10291/2023")';
COMMENT ON COLUMN court_decisions.created_at IS 'Дата и время создания записи';
COMMENT ON COLUMN court_decisions.updated_at IS 'Дата и время последнего обновления записи';

--changeset admin:add-filds_table_clients
ALTER TABLE clients
    add column full_name_genitive varchar(255);
ALTER TABLE clients
    add column full_name_short_genitive varchar(255);

--changeset admin:del-filds_table_clients_v01
ALTER TABLE clients
    DROP COLUMN IF EXISTS court_name,
    DROP COLUMN IF EXISTS case_number,
    DROP COLUMN IF EXISTS court_decision_date,
    DROP COLUMN IF EXISTS procedure_type;


