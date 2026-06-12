-- liquibase formatted sql

-- changeset admin:seed-recipient-orgs
-- comment: Справочник органов для RecipientLookupService (чтобы не возвращал null)
INSERT INTO recipient_orgs (id, doc_type, org_name, org_address, org_note)
VALUES ('10000000-0000-0000-0000-000000000001', 'fsps', 'Отдел судебных приставов',
        'г. Ростов-на-Дону, ул. Примерная, 1', 'Начальнику отделения'),
       ('10000000-0000-0000-0000-000000000002', 'court', 'Арбитражный суд', 'г. Ростов-на-Дону, ул. Судеab, 1',
        'В канцелярию'),
       ('10000000-0000-0000-0000-000000000003', 'mifns', 'Межрайонная ИФНС', 'г. Ростов-на-Дону, ул. Налоговая, 1',
        'Руководителю инспекции') ON CONFLICT (doc_type) DO NOTHING;
--rollback DELETE FROM recipient_orgs WHERE id IN ('10000000-0000-0000-0000-000000000001', '10000000-0000-0000-0000-000000000002', '10000000-0000-0000-0000-000000000003');