-- ============================================================
--  Seed data for the AoE4 Civilization Randomizer
--  Edit this file to add, remove, or rename civilizations.
--
--  Format of each INSERT:
--    INSERT INTO civilization (name, dlc, icon_path, enabled)
--    SELECT '<CivName>', '<DLC group>', '/images/civs/<icon-file>.png', TRUE
--    WHERE NOT EXISTS (SELECT 1 FROM civilization WHERE name = '<CivName>');
--
--  The WHERE NOT EXISTS guard means:
--    • First run  → rows are inserted.
--    • Later runs → existing rows are left untouched (your enable/disable
--                   settings are preserved across restarts).
--
--  DLC groups currently in use:
--    "Base Game"
--    "The Sultans Ascend"
--    "Sultans Ascend Variant"
--    "Knights of the Cross and Rose"
--    "Dynasties of the East"
--    "Yue Fei's Legacy"
-- ============================================================

-- Base Game (10 civilizations)
INSERT INTO civilization (name, dlc, icon_path, enabled) SELECT 'English',           'Base Game', '/images/civs/english.png', TRUE WHERE NOT EXISTS (SELECT 1 FROM civilization WHERE name = 'English');
INSERT INTO civilization (name, dlc, icon_path, enabled) SELECT 'Chinese',           'Base Game', '/images/civs/chinese.png', TRUE WHERE NOT EXISTS (SELECT 1 FROM civilization WHERE name = 'Chinese');
INSERT INTO civilization (name, dlc, icon_path, enabled) SELECT 'French',            'Base Game', '/images/civs/french.png', TRUE WHERE NOT EXISTS (SELECT 1 FROM civilization WHERE name = 'French');
INSERT INTO civilization (name, dlc, icon_path, enabled) SELECT 'Holy Roman Empire', 'Base Game', '/images/civs/holy-roman-empire.png', TRUE WHERE NOT EXISTS (SELECT 1 FROM civilization WHERE name = 'Holy Roman Empire');
INSERT INTO civilization (name, dlc, icon_path, enabled) SELECT 'Mongols',           'Base Game', '/images/civs/mongols.png', TRUE WHERE NOT EXISTS (SELECT 1 FROM civilization WHERE name = 'Mongols');
INSERT INTO civilization (name, dlc, icon_path, enabled) SELECT 'Rus',               'Base Game', '/images/civs/rus.png', TRUE WHERE NOT EXISTS (SELECT 1 FROM civilization WHERE name = 'Rus');
INSERT INTO civilization (name, dlc, icon_path, enabled) SELECT 'Delhi Sultanate',   'Base Game', '/images/civs/delhi-sultanate.png', TRUE WHERE NOT EXISTS (SELECT 1 FROM civilization WHERE name = 'Delhi Sultanate');
INSERT INTO civilization (name, dlc, icon_path, enabled) SELECT 'Abbasid Dynasty',   'Base Game', '/images/civs/abbasid-dynasty.png', TRUE WHERE NOT EXISTS (SELECT 1 FROM civilization WHERE name = 'Abbasid Dynasty');
INSERT INTO civilization (name, dlc, icon_path, enabled) SELECT 'Ottomans',          'Base Game', '/images/civs/ottomans.png', TRUE WHERE NOT EXISTS (SELECT 1 FROM civilization WHERE name = 'Ottomans');
INSERT INTO civilization (name, dlc, icon_path, enabled) SELECT 'Malians',           'Base Game', '/images/civs/malians.png', TRUE WHERE NOT EXISTS (SELECT 1 FROM civilization WHERE name = 'Malians');

-- The Sultans Ascend DLC (2 civilizations)
INSERT INTO civilization (name, dlc, icon_path, enabled) SELECT 'Byzantines', 'The Sultans Ascend', '/images/civs/byzantines.png', TRUE WHERE NOT EXISTS (SELECT 1 FROM civilization WHERE name = 'Byzantines');
INSERT INTO civilization (name, dlc, icon_path, enabled) SELECT 'Japanese',   'The Sultans Ascend', '/images/civs/japanese.png', TRUE WHERE NOT EXISTS (SELECT 1 FROM civilization WHERE name = 'Japanese');

-- Sultans Ascend Variant civilizations (4 civilizations)
INSERT INTO civilization (name, dlc, icon_path, enabled) SELECT 'Ayyubids',           'Sultans Ascend Variant', '/images/civs/ayyubids.png', TRUE WHERE NOT EXISTS (SELECT 1 FROM civilization WHERE name = 'Ayyubids');
INSERT INTO civilization (name, dlc, icon_path, enabled) SELECT 'Jeanne d''Arc',      'Sultans Ascend Variant', '/images/civs/jeanne-d-arc.png', TRUE WHERE NOT EXISTS (SELECT 1 FROM civilization WHERE name = 'Jeanne d''Arc');
INSERT INTO civilization (name, dlc, icon_path, enabled) SELECT 'Order of the Dragon', 'Sultans Ascend Variant', '/images/civs/order-of-the-dragon.png', TRUE WHERE NOT EXISTS (SELECT 1 FROM civilization WHERE name = 'Order of the Dragon');
INSERT INTO civilization (name, dlc, icon_path, enabled) SELECT 'Zhu Xi''s Legacy',   'Sultans Ascend Variant', '/images/civs/zhu-xis-legacy.png', TRUE WHERE NOT EXISTS (SELECT 1 FROM civilization WHERE name = 'Zhu Xi''s Legacy');

-- Knights of the Cross and Rose DLC (2 civilizations)
INSERT INTO civilization (name, dlc, icon_path, enabled) SELECT 'House of Lancaster', 'Knights of the Cross and Rose', '/images/civs/house-of-lancaster.png', TRUE WHERE NOT EXISTS (SELECT 1 FROM civilization WHERE name = 'House of Lancaster');
INSERT INTO civilization (name, dlc, icon_path, enabled) SELECT 'Knights Templar',    'Knights of the Cross and Rose', '/images/civs/knights-templar.png', TRUE WHERE NOT EXISTS (SELECT 1 FROM civilization WHERE name = 'Knights Templar');

-- Dynasties of the East DLC (4 civilizations)
INSERT INTO civilization (name, dlc, icon_path, enabled) SELECT 'Golden Horde',      'Dynasties of the East', '/images/civs/golden-horde.png', TRUE WHERE NOT EXISTS (SELECT 1 FROM civilization WHERE name = 'Golden Horde');
INSERT INTO civilization (name, dlc, icon_path, enabled) SELECT 'Macedonian Dynasty', 'Dynasties of the East', '/images/civs/macedonian-dynasty.png', TRUE WHERE NOT EXISTS (SELECT 1 FROM civilization WHERE name = 'Macedonian Dynasty');
INSERT INTO civilization (name, dlc, icon_path, enabled) SELECT 'Sengoku Daimyo',    'Dynasties of the East', '/images/civs/sengoku-daimyo.png', TRUE WHERE NOT EXISTS (SELECT 1 FROM civilization WHERE name = 'Sengoku Daimyo');
INSERT INTO civilization (name, dlc, icon_path, enabled) SELECT 'Tughlaq Dynasty',   'Dynasties of the East', '/images/civs/tughlaq-dynasty.png', TRUE WHERE NOT EXISTS (SELECT 1 FROM civilization WHERE name = 'Tughlaq Dynasty');

-- Yue Fei's Legacy DLC (1 civilization)
INSERT INTO civilization (name, dlc, icon_path, enabled) SELECT 'Jin Dynasty', 'Yue Fei''s Legacy', '/images/civs/jin-dynasty.png', TRUE WHERE NOT EXISTS (SELECT 1 FROM civilization WHERE name = 'Jin Dynasty');

-- Ensure existing rows from older app versions get icon paths populated.
UPDATE civilization SET icon_path = '/images/civs/english.png' WHERE name = 'English' AND (icon_path IS NULL OR icon_path = '');
UPDATE civilization SET icon_path = '/images/civs/chinese.png' WHERE name = 'Chinese' AND (icon_path IS NULL OR icon_path = '');
UPDATE civilization SET icon_path = '/images/civs/french.png' WHERE name = 'French' AND (icon_path IS NULL OR icon_path = '');
UPDATE civilization SET icon_path = '/images/civs/holy-roman-empire.png' WHERE name = 'Holy Roman Empire' AND (icon_path IS NULL OR icon_path = '');
UPDATE civilization SET icon_path = '/images/civs/mongols.png' WHERE name = 'Mongols' AND (icon_path IS NULL OR icon_path = '');
UPDATE civilization SET icon_path = '/images/civs/rus.png' WHERE name = 'Rus' AND (icon_path IS NULL OR icon_path = '');
UPDATE civilization SET icon_path = '/images/civs/delhi-sultanate.png' WHERE name = 'Delhi Sultanate' AND (icon_path IS NULL OR icon_path = '');
UPDATE civilization SET icon_path = '/images/civs/abbasid-dynasty.png' WHERE name = 'Abbasid Dynasty' AND (icon_path IS NULL OR icon_path = '');
UPDATE civilization SET icon_path = '/images/civs/ottomans.png' WHERE name = 'Ottomans' AND (icon_path IS NULL OR icon_path = '');
UPDATE civilization SET icon_path = '/images/civs/malians.png' WHERE name = 'Malians' AND (icon_path IS NULL OR icon_path = '');
UPDATE civilization SET icon_path = '/images/civs/byzantines.png' WHERE name = 'Byzantines' AND (icon_path IS NULL OR icon_path = '');
UPDATE civilization SET icon_path = '/images/civs/japanese.png' WHERE name = 'Japanese' AND (icon_path IS NULL OR icon_path = '');
UPDATE civilization SET icon_path = '/images/civs/ayyubids.png' WHERE name = 'Ayyubids' AND (icon_path IS NULL OR icon_path = '');
UPDATE civilization SET icon_path = '/images/civs/jeanne-d-arc.png' WHERE name = 'Jeanne d''Arc' AND (icon_path IS NULL OR icon_path = '');
UPDATE civilization SET icon_path = '/images/civs/order-of-the-dragon.png' WHERE name = 'Order of the Dragon' AND (icon_path IS NULL OR icon_path = '');
UPDATE civilization SET icon_path = '/images/civs/zhu-xis-legacy.png' WHERE name = 'Zhu Xi''s Legacy' AND (icon_path IS NULL OR icon_path = '');
UPDATE civilization SET icon_path = '/images/civs/house-of-lancaster.png' WHERE name = 'House of Lancaster' AND (icon_path IS NULL OR icon_path = '');
UPDATE civilization SET icon_path = '/images/civs/knights-templar.png' WHERE name = 'Knights Templar' AND (icon_path IS NULL OR icon_path = '');
UPDATE civilization SET icon_path = '/images/civs/golden-horde.png' WHERE name = 'Golden Horde' AND (icon_path IS NULL OR icon_path = '');
UPDATE civilization SET icon_path = '/images/civs/macedonian-dynasty.png' WHERE name = 'Macedonian Dynasty' AND (icon_path IS NULL OR icon_path = '');
UPDATE civilization SET icon_path = '/images/civs/sengoku-daimyo.png' WHERE name = 'Sengoku Daimyo' AND (icon_path IS NULL OR icon_path = '');
UPDATE civilization SET icon_path = '/images/civs/tughlaq-dynasty.png' WHERE name = 'Tughlaq Dynasty' AND (icon_path IS NULL OR icon_path = '');
UPDATE civilization SET icon_path = '/images/civs/jin-dynasty.png' WHERE name = 'Jin Dynasty' AND (icon_path IS NULL OR icon_path = '');
