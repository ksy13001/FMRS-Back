UPDATE fmplayer fm
JOIN player p ON fm.player_id = p.id
SET fm.player_id = NULL
WHERE p.mapping_status = 'MATCHED'
  AND (fm.first_name  != p.first_name
   OR fm.last_name   != p.last_name
   OR fm.birth       != p.birth
   OR fm.nation_name != p.nation_name);


UPDATE player p
SET p.mapping_status = 'UNMAPPED'
WHERE p.mapping_status = 'MATCHED'
AND p.id NOT IN (SELECT player_id FROM fmplayer WHERE player_id IS NOT NULL);