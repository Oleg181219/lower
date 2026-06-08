-- liquibase formatted sql

--changeset admin:create-court-orgs-table
--comment: Создание справочника органов-получателей для генерации документов
CREATE TABLE IF NOT EXISTS court_orgs (
    id UUID DEFAULT gen_random_uuid_v7() PRIMARY KEY,
    doc_type VARCHAR(50) NOT NULL, --Тип органа / код шаблона. Примеры: fsps (судебные приставы), court (суд), gibdd (ГИБДД), mifns (налоговая)
    region_code VARCHAR(10), --Код региона Может быть NULL для федеральных органов, у которых нет регионального деления (например, Росавиация, Роспатент).
    org_name VARCHAR(500) NOT NULL, --Полное наименование органа
    org_address TEXT NOT NULL,
    org_note TEXT,
    is_active BOOLEAN DEFAULT TRUE NOT NULL, --Флаг активности. Позволяет временно «отключить» орган в справочнике без физического удаления записи из БД. Если орган реорганизован или адрес изменился
    created_at TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP NOT NULL,
    updated_at TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP NOT NULL,
    UNIQUE(doc_type, region_code)
    );

-- Индексы для быстрого поиска
CREATE INDEX IF NOT EXISTS idx_court_orgs_type_region ON court_orgs(doc_type, region_code) WHERE is_active = TRUE;
CREATE INDEX IF NOT EXISTS idx_court_orgs_type_active ON court_orgs(doc_type) WHERE is_active = TRUE AND region_code IS NULL;
--rollback DROP TABLE IF EXISTS court_orgs;

--changeset admin:add-trigger-to-court-orgs
--comment: Триггер для автоматического обновления updated_at
CREATE TRIGGER update_court_orgs_updated_at
    BEFORE UPDATE
    ON court_orgs
    FOR EACH ROW
    EXECUTE FUNCTION update_updated_at_column();
--rollback DROP TRIGGER IF EXISTS update_court_orgs_updated_at ON court_orgs;

--changeset admin:seed-court-orgs-fsps
--comment: Справочник ФССП по регионам
INSERT INTO court_orgs (doc_type, region_code, org_name, org_address, org_note)
VALUES ('fsps', '61', 'Отдел судебных приставов по Ростовской области', '344038, г. Ростов-на-Дону, ул. Суворова, 65',
        'Управление ФССП по Ростовской области'),
       ('fsps', '77', 'Отдел судебных приставов по г. Москве', '107996, г. Москва, ул. Кузнецкий Мост, д. 12/3',
        'Управление ФССП по г. Москве'),
       ('fsps', '78', 'Отдел судебных приставов по г. Санкт-Петербургу',
        '191015, г. Санкт-Петербург, ул. Красного Текстильщика, д. 10-12',
        'Управление ФССП по г. Санкт-Петербургу') ON CONFLICT (doc_type, region_code) DO
UPDATE SET
    org_name = EXCLUDED.org_name,
    org_address = EXCLUDED.org_address,
    org_note = EXCLUDED.org_note,
    updated_at = NOW();
--rollback DELETE FROM court_orgs WHERE doc_type = 'fsps';

--changeset admin:seed-court-orgs-gibdd
--comment: Справочник ГИБДД по регионам
INSERT INTO court_orgs (doc_type, region_code, org_name, org_address, org_note)
VALUES ('gibdd', '61', 'МРЭО ГИБДД ГУ МВД России по Ростовской области',
        '344038, г. Ростов-на-Дону, ул. 1-я Конная Армия, 36', 'Межрайонный регистрационно-экзаменационный отдел'),
       ('gibdd', '77', 'ГИБДД ГУ МВД России по г. Москве', '127994, г. Москва, ул. Садовая-Самотечная, д. 1',
        'Государственная инспекция безопасности дорожного движения') ON CONFLICT (doc_type, region_code) DO
UPDATE SET
    org_name = EXCLUDED.org_name,
    org_address = EXCLUDED.org_address,
    org_note = EXCLUDED.org_note,
    updated_at = NOW();
--rollback DELETE FROM court_orgs WHERE doc_type = 'gibdd';

--changeset admin:seed-court-orgs-bti
--comment: Справочник БТИ по регионам
INSERT INTO court_orgs (doc_type, region_code, org_name, org_address, org_note)
VALUES ('bti', '61', 'Ростовское областное БТИ', '344038, г. Ростов-на-Дону, пр. Ворошиловский, 51/2', 'Областное БТИ'),
       ('bti', '77', 'Московское городское БТИ', '105066, г. Москва, ул. Земляной Вал, д. 53',
        'Городское БТИ') ON CONFLICT (doc_type, region_code) DO
UPDATE SET
    org_name = EXCLUDED.org_name,
    org_address = EXCLUDED.org_address,
    org_note = EXCLUDED.org_note,
    updated_at = NOW();
--rollback DELETE FROM court_orgs WHERE doc_type = 'bti';

--changeset admin:seed-court-orgs-mifns
--comment: Справочник МИФНС (налоговая) по регионам
INSERT INTO court_orgs (doc_type, region_code, org_name, org_address, org_note)
VALUES ('mifns', '61', 'Межрайонная ИФНС России № 1 по Ростовской области',
        '344038, г. Ростов-на-Дону, ул. Большая Садовая, 235', 'Налоговая инспекция'),
       ('mifns', '77', 'Межрайонная ИФНС России № 46 по г. Москве',
        '125373, г. Москва, Походный проезд, вл. 3, корп. 1',
        'Налоговая инспекция') ON CONFLICT (doc_type, region_code) DO
UPDATE SET
    org_name = EXCLUDED.org_name,
    org_address = EXCLUDED.org_address,
    org_note = EXCLUDED.org_note,
    updated_at = NOW();
--rollback DELETE FROM court_orgs WHERE doc_type = 'mifns';

--changeset admin:seed-court-orgs-rosimushchestvo
--comment: Справочник РосИмущества по регионам
INSERT INTO court_orgs (doc_type, region_code, org_name, org_address, org_note)
VALUES ('rosimushchestvo', '61', 'Территориальное управление Росимущества в Ростовской области',
        '344038, г. Ростов-на-Дону, ул. Социалистическая, 74', 'ТУ Росимущества'),
       ('rosimushchestvo', '77', 'Территориальное управление Росимущества в г. Москве',
        '105066, г. Москва, ул. Басманная, д. 30, стр. 1', 'ТУ Росимущества') ON CONFLICT (doc_type, region_code) DO
UPDATE SET
    org_name = EXCLUDED.org_name,
    org_address = EXCLUDED.org_address,
    org_note = EXCLUDED.org_note,
    updated_at = NOW();
--rollback DELETE FROM court_orgs WHERE doc_type = 'rosimushchestvo';

--changeset admin:seed-court-orgs-mchs
--comment: Справочник МЧС (маломерные суда) по регионам
INSERT INTO court_orgs (doc_type, region_code, org_name, org_address, org_note)
VALUES ('mchs', '61', 'Главное управление МЧС России по Ростовской области',
        '344038, г. Ростов-на-Дону, ул. Тургеневская, 48', 'ГИМС МЧС России'),
       ('mchs', '77', 'Главное управление МЧС России по г. Москве', '107078, г. Москва, ул. Новорязанская, д. 26/2',
        'ГИМС МЧС России') ON CONFLICT (doc_type, region_code) DO
UPDATE SET
    org_name = EXCLUDED.org_name,
    org_address = EXCLUDED.org_address,
    org_note = EXCLUDED.org_note,
    updated_at = NOW();
--rollback DELETE FROM court_orgs WHERE doc_type = 'mchs';

--changeset admin:seed-court-orgs-rostekhnadzor
--comment: Справочник Ростехнадзора (Гостехнадзор) по регионам
INSERT INTO court_orgs (doc_type, region_code, org_name, org_address, org_note)
VALUES ('rostekhnadzor', '61',
        'Управление государственного надзора за техническим состоянием самоходных машин и других видов техники Ростовской области (Ростовоблгостехнадзор)',
        '344038, г. Ростов-на-Дону, пр. Михаила Нагибина, 14 А',
        'Управление гостехнадзора по региону регистрации должника'),
       ('rostekhnadzor', '77', 'Инспекция гостехнадзора г. Москвы',
        '107078, г. Москва, ул. Новая Басманная, д. 23, стр. 1',
        'Инспекция гостехнадзора') ON CONFLICT (doc_type, region_code) DO
UPDATE SET
    org_name = EXCLUDED.org_name,
    org_address = EXCLUDED.org_address,
    org_note = EXCLUDED.org_note,
    updated_at = NOW();
--rollback DELETE FROM court_orgs WHERE doc_type = 'rostekhnadzor';

--changeset admin:seed-court-orgs-rosgruard
--comment: Справочник Росгвардии по регионам
INSERT INTO court_orgs (doc_type, region_code, org_name, org_address, org_note)
VALUES ('rosgruard', '61', 'Управление Росгвардии по Ростовской области',
        '344038, г. Ростов-на-Дону, ул. Шеболдаева, 4/3', 'Управление Росгвардии по региону регистрации должника'),
       ('rosgruard', '77', 'Управление Росгвардии по г. Москве', '107996, г. Москва, ул. Варварка, д. 14',
        'Управление Росгвардии') ON CONFLICT (doc_type, region_code) DO
UPDATE SET
    org_name = EXCLUDED.org_name,
    org_address = EXCLUDED.org_address,
    org_note = EXCLUDED.org_note,
    updated_at = NOW();
--rollback DELETE FROM court_orgs WHERE doc_type = 'rosgruard';

--changeset admin:seed-court-orgs-osfr
--comment: Справочник ОСФР (Социальный фонд России) по регионам
INSERT INTO court_orgs (doc_type, region_code, org_name, org_address, org_note)
VALUES ('osfr', '61', 'Отделение Социального фонда России по Ростовской области',
        '344038, г. Ростов-на-Дону, ул. Суворова, 53', 'Отделение СФР'),
       ('osfr', '77', 'Отделение Социального фонда России по г. Москве и Московской области',
        '105077, г. Москва, ул. Верхняя Красносельская, д. 15, стр. 1',
        'Отделение СФР') ON CONFLICT (doc_type, region_code) DO
UPDATE SET
    org_name = EXCLUDED.org_name,
    org_address = EXCLUDED.org_address,
    org_note = EXCLUDED.org_note,
    updated_at = NOW();
--rollback DELETE FROM court_orgs WHERE doc_type = 'osfr';

--changeset admin:seed-court-orgs-court
--comment: Справочник арбитражных судов по регионам
INSERT INTO court_orgs (doc_type, region_code, org_name, org_address, org_note)
VALUES ('court', '61', 'Арбитражный суд Ростовской области', '344038, г. Ростов-на-Дону, ул. Пушкинская, 36',
        'Арбитражный суд'),
       ('court', '77', 'Арбитражный суд города Москвы', '115191, г. Москва, ул. Большая Тульская, д. 17',
        'Арбитражный суд'),
       ('court', '78', 'Арбитражный суд города Санкт-Петербурга и Ленинградской области',
        '190000, г. Санкт-Петербург, ул. Суворовский пр., д. 1',
        'Арбитражный суд') ON CONFLICT (doc_type, region_code) DO
UPDATE SET
    org_name = EXCLUDED.org_name,
    org_address = EXCLUDED.org_address,
    org_note = EXCLUDED.org_note,
    updated_at = NOW();
--rollback DELETE FROM court_orgs WHERE doc_type = 'court';

--changeset admin:seed-court-orgs-central
--comment: Федеральные органы (без привязки к региону)
INSERT INTO court_orgs (doc_type, region_code, org_name, org_address, org_note)
VALUES ('rosaviatsia', NULL, 'Федеральное агентство воздушного транспорта (Росавиация)',
        '125167, г. Москва, Ленинградский проспект, д. 37, корп. 2', 'Центральный аппарат'),
       ('rospatent', NULL, 'Федеральная служба по интеллектуальной собственности (Роспатент)',
        '125993, г. Москва, Бережковская наб., д. 30, корп. 1',
        'Центральный аппарат') ON CONFLICT (doc_type, region_code) DO
UPDATE SET
    org_name = EXCLUDED.org_name,
    org_address = EXCLUDED.org_address,
    org_note = EXCLUDED.org_note,
    updated_at = NOW();
--rollback DELETE FROM court_orgs WHERE doc_type IN ('rosaviatsia', 'rospatent');