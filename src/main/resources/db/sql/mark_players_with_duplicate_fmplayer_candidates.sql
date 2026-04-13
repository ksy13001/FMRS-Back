UPDATE player p
SET p.mapping_status = 'DUPLICATE'
WHERE p.mapping_status = 'UNMAPPED'
  AND (
      SELECT COUNT(DISTINCT fm.fm_uid)
      FROM fmplayer fm
      WHERE fm.first_name  = p.first_name
        AND fm.last_name   = p.last_name
        AND fm.birth       = p.birth
        AND fm.nation_name = p.nation_name
        AND fm.player_id IS NULL
  ) > 1