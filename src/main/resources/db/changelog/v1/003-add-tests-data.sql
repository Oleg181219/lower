-- liquibase formatted sql

-- changeset admin:seed-test-users
-- comment: Тестовые пользователи. ПАРОЛЬ ДЛЯ ВСЕХ: 123
-- Хэш ниже - это валидный BCrypt хэш для строки "123"
INSERT INTO users (id, username, password, role, created_at, updated_at)
VALUES ('11111111-1111-1111-1111-111111111111', 'admin@lower.ru',
        '$2a$10$EixZaYVK1fsbw1ZfbX3OXePaWxn96p36WQoeG6Lruj3vjPGfa3ZlW', 'ADMIN', NOW(), NOW()),
       ('22222222-2222-2222-2222-222222222222', 'svetlanaot@yandex.ru',
        '$2a$10$EixZaYVK1fsbw1ZfbX3OXePaWxn96p36WQoeG6Lruj3vjPGfa3ZlW', 'OWNER', NOW(), NOW()),
       ('33333333-3333-3333-3333-333333333333', 'worker@lower.ru',
        '$2a$10$EixZaYVK1fsbw1ZfbX3OXePaWxn96p36WQoeG6Lruj3vjPGfa3ZlW', 'WORKER', NOW(),
        NOW()) ON CONFLICT (id) DO NOTHING;
--rollback DELETE FROM users WHERE id IN ('11111111-1111-1111-1111-111111111111', '22222222-2222-2222-2222-222222222222', '33333333-3333-3333-3333-333333333333');

-- changeset admin:seed-test-owners
-- comment: Тестовый арбитражный управляющий (Владелец)
INSERT INTO owners (id, user_id, full_name, full_name_short, mail_address, email, sro_name, sro_inn, sro_ogrn,
                    sro_address, user_inn, user_snils, created_at, updated_at)
VALUES ('aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa', '22222222-2222-2222-2222-222222222222',
        'Степаньянц Светлана Анатольевна', 'Степаньянц С.А.',
        '344082, г. Ростов-на-Дону, пер. Халтуринский, 4, оф. 6', 'svetlanaot@yandex.ru',
        'СРО АУ "Лига"', '5836140708', '1045803007326', 'г. Пенза, ул. Володарского, 9',
        '616511012560', '14063088738', NOW(), NOW()) ON CONFLICT (id) DO NOTHING;
--rollback DELETE FROM owners WHERE id = 'aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa';

-- changeset admin:seed-test-clients
-- comment: Тестовые должники (клиенты) с данными судов
INSERT INTO clients (id, full_name, birth_date, birth_place, inn, snils, address, court_name, case_number,
                     court_decision_date, procedure_type, owner_id, created_at, updated_at)
VALUES ('cccccccc-cccc-cccc-cccc-cccccccccccc', 'Подрезов Александр Александрович', '1990-09-07',
        'Ростовская область, г. Новочеркасск', '615018201246', '163-409-915 71',
        '344000, г. Ростов-на-Дону, ул. Пушкинская, д. 10, кв. 5', 'Арбитражный суд Ростовской области',
        'А53-12345/2023', '2023-05-15', 'Реализация имущества', 'aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa', NOW(), NOW()),

       ('dddddddd-dddd-dddd-dddd-dddddddddddd', 'Иванов Иван Иванович', '1985-01-15', 'г. Москва', '770112345678',
        '112-233-445 56', '101000, г. Москва, ул. Тверская, д. 1, кв. 1', 'Арбитражный суд города Москвы',
        'А40-98765/2024', '2024-02-20', 'Реструктуризация долгов', 'aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa', NOW(),
        NOW()) ON CONFLICT (id) DO NOTHING;
--rollback DELETE FROM clients WHERE id IN ('cccccccc-cccc-cccc-cccc-cccccccccccc', 'dddddddd-dddd-dddd-dddd-dddddddddddd');

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