UPDATE player p
SET p.mapping_status = 'MATCHED',
    p.mapping_method = 'EXACT_4KEY'
WHERE p.mapping_status = 'NO_MATCH'
  AND EXISTS (
      SELECT 1
      FROM fmplayer fm
      WHERE fm.player_id = p.id
  );
