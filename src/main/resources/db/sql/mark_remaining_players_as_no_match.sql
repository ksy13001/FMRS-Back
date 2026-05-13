UPDATE player p
SET p.mapping_status = 'NO_MATCH',
    p.mapping_method = NULL
WHERE p.mapping_status = 'UNMAPPED';
