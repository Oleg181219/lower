-- liquibase formatted sql

--changeset admin:create-court-orgs-table
--comment: Создание справочника органов-получателей для генерации документов
CREATE TABLE IF NOT EXISTS court_orgs (
    id UUID DEFAULT gen_random_uuid_v7() PRIMARY KEY,
    doc_type VARCHAR(50) NOT NULL UNIQUE,
    org_name VARCHAR(500) NOT NULL,
    org_address TEXT NOT NULL,
    org_note TEXT
);
--rollback DROP TABLE IF EXISTS court_orgs;

--changeset admin:seed-court-orgs-dictionary
--comment: Наполнение справочника реальными данными органов
INSERT INTO court_orgs (doc_type, org_name, org_address, org_note) VALUES
    (
        'rostekhnadzor',
        'Управление государственного надзора за техническим состоянием самоходных машин и других видов техники Ростовской области (Ростовоблгостехнадзор)',
        '344038, г. Ростов-на-Дону, пр. Михаила Нагибина, 14 А',
        'Управление гостехнадзора по региону регистрации должника'
    ),
    (
        'court',
        'Шахтинский городской суд Ростовской области',
        '346500, Ростовская область, г. Шахты, ул. Черенкова, д. 17 А',
        'Районный/городской суд по месту регистрации должника'
    ),
    (
        'rosaviatsia',
        'ФЕДЕРАЛЬНОЕ АГЕНТСТВО ВОЗДУШНОГО ТРАНСПОРТА (РОСАВИАЦИЯ)',
        '125167, г. Москва, Ленинградский пр-т, д. 37, корп. 2',
        NULL
    ),
    (
        'rosgruard',
        'Управление Росгвардии по Ростовской области',
        '344038, г. Ростов-на-Дону, ул. Шеболдаева, 4/3',
        'Управление Росгвардии по региону регистрации должника'
    ),
    (
        'fsps',
        'Новочеркасский Городской отдел судебных приставов Ростовской области',
        '346429, Ростовская обл., г. Новочеркасск, ул. Кавказская, 75',
        'Отдел судебных приставов по месту регистрации должника'
    )
ON CONFLICT (doc_type) DO UPDATE SET
    org_name = EXCLUDED.org_name,
    org_address = EXCLUDED.org_address,
    org_note = EXCLUDED.org_note;
--rollback DELETE FROM recipient_orgs WHERE doc_type IN ('rostekhnadzor', 'court', 'rosaviatsia', 'rosgruard', 'fsps');