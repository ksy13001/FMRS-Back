ALTER TABLE player
    ADD COLUMN mapping_method ENUM(
        'EXACT_4KEY',
        'FUZZY_JARO_WINKLER',
        'MANUAL'
    ) NULL DEFAULT NULL;