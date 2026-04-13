UPDATE player p
SET p.mapping_status = 'NO_MATCH'
WHERE p.mapping_status = 'UNMAPPED';
