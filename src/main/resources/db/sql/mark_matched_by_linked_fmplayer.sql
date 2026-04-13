UPDATE player p
SET p.mapping_status = 'MATCHED'
WHERE p.mapping_status = 'UNMAPPED'
  AND EXISTS (
    SELECT 1 FROM fmplayer fm WHERE fm.player_id = p.id
  )
