-- ============================================================
--  Seed data for the AoE4 Civilization Randomizer
--  Edit this file to add, remove, or rename civilizations.
--
--  Format of each INSERT:
--    INSERT INTO civilization (name, dlc, enabled)
--    SELECT '<CivName>', '<DLC group>', TRUE
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
INSERT INTO civilization (name, dlc, enabled) SELECT 'English',           'Base Game', TRUE WHERE NOT EXISTS (SELECT 1 FROM civilization WHERE name = 'English');
INSERT INTO civilization (name, dlc, enabled) SELECT 'Chinese',           'Base Game', TRUE WHERE NOT EXISTS (SELECT 1 FROM civilization WHERE name = 'Chinese');
INSERT INTO civilization (name, dlc, enabled) SELECT 'French',            'Base Game', TRUE WHERE NOT EXISTS (SELECT 1 FROM civilization WHERE name = 'French');
INSERT INTO civilization (name, dlc, enabled) SELECT 'Holy Roman Empire', 'Base Game', TRUE WHERE NOT EXISTS (SELECT 1 FROM civilization WHERE name = 'Holy Roman Empire');
INSERT INTO civilization (name, dlc, enabled) SELECT 'Mongols',           'Base Game', TRUE WHERE NOT EXISTS (SELECT 1 FROM civilization WHERE name = 'Mongols');
INSERT INTO civilization (name, dlc, enabled) SELECT 'Rus',               'Base Game', TRUE WHERE NOT EXISTS (SELECT 1 FROM civilization WHERE name = 'Rus');
INSERT INTO civilization (name, dlc, enabled) SELECT 'Delhi Sultanate',   'Base Game', TRUE WHERE NOT EXISTS (SELECT 1 FROM civilization WHERE name = 'Delhi Sultanate');
INSERT INTO civilization (name, dlc, enabled) SELECT 'Abbasid Dynasty',   'Base Game', TRUE WHERE NOT EXISTS (SELECT 1 FROM civilization WHERE name = 'Abbasid Dynasty');
INSERT INTO civilization (name, dlc, enabled) SELECT 'Ottomans',          'Base Game', TRUE WHERE NOT EXISTS (SELECT 1 FROM civilization WHERE name = 'Ottomans');
INSERT INTO civilization (name, dlc, enabled) SELECT 'Malians',           'Base Game', TRUE WHERE NOT EXISTS (SELECT 1 FROM civilization WHERE name = 'Malians');

-- The Sultans Ascend DLC (2 civilizations)
INSERT INTO civilization (name, dlc, enabled) SELECT 'Byzantines', 'The Sultans Ascend', TRUE WHERE NOT EXISTS (SELECT 1 FROM civilization WHERE name = 'Byzantines');
INSERT INTO civilization (name, dlc, enabled) SELECT 'Japanese',   'The Sultans Ascend', TRUE WHERE NOT EXISTS (SELECT 1 FROM civilization WHERE name = 'Japanese');

-- Sultans Ascend Variant civilizations (4 civilizations)
INSERT INTO civilization (name, dlc, enabled) SELECT 'Ayyubids',           'Sultans Ascend Variant', TRUE WHERE NOT EXISTS (SELECT 1 FROM civilization WHERE name = 'Ayyubids');
INSERT INTO civilization (name, dlc, enabled) SELECT 'Jeanne d''Arc',      'Sultans Ascend Variant', TRUE WHERE NOT EXISTS (SELECT 1 FROM civilization WHERE name = 'Jeanne d''Arc');
INSERT INTO civilization (name, dlc, enabled) SELECT 'Order of the Dragon', 'Sultans Ascend Variant', TRUE WHERE NOT EXISTS (SELECT 1 FROM civilization WHERE name = 'Order of the Dragon');
INSERT INTO civilization (name, dlc, enabled) SELECT 'Zhu Xi''s Legacy',   'Sultans Ascend Variant', TRUE WHERE NOT EXISTS (SELECT 1 FROM civilization WHERE name = 'Zhu Xi''s Legacy');

-- Knights of the Cross and Rose DLC (2 civilizations)
INSERT INTO civilization (name, dlc, enabled) SELECT 'House of Lancaster', 'Knights of the Cross and Rose', TRUE WHERE NOT EXISTS (SELECT 1 FROM civilization WHERE name = 'House of Lancaster');
INSERT INTO civilization (name, dlc, enabled) SELECT 'Knights Templar',    'Knights of the Cross and Rose', TRUE WHERE NOT EXISTS (SELECT 1 FROM civilization WHERE name = 'Knights Templar');

-- Dynasties of the East DLC (4 civilizations)
INSERT INTO civilization (name, dlc, enabled) SELECT 'Golden Horde',      'Dynasties of the East', TRUE WHERE NOT EXISTS (SELECT 1 FROM civilization WHERE name = 'Golden Horde');
INSERT INTO civilization (name, dlc, enabled) SELECT 'Macedonian Dynasty', 'Dynasties of the East', TRUE WHERE NOT EXISTS (SELECT 1 FROM civilization WHERE name = 'Macedonian Dynasty');
INSERT INTO civilization (name, dlc, enabled) SELECT 'Sengoku Daimyo',    'Dynasties of the East', TRUE WHERE NOT EXISTS (SELECT 1 FROM civilization WHERE name = 'Sengoku Daimyo');
INSERT INTO civilization (name, dlc, enabled) SELECT 'Tughlaq Dynasty',   'Dynasties of the East', TRUE WHERE NOT EXISTS (SELECT 1 FROM civilization WHERE name = 'Tughlaq Dynasty');

-- Yue Fei's Legacy DLC (1 civilization)
INSERT INTO civilization (name, dlc, enabled) SELECT 'Jin Dynasty', 'Yue Fei''s Legacy', TRUE WHERE NOT EXISTS (SELECT 1 FROM civilization WHERE name = 'Jin Dynasty');
