SELECT MIN(p.id) AS player_id, MIN(fm.fm_uid) AS fm_uid
FROM player p
JOIN fmplayer fm
  ON p.first_name  = fm.first_name
 AND p.last_name   = fm.last_name
 AND p.birth       = fm.birth
 AND p.nation_name = fm.nation_name
WHERE p.mapping_status = 'UNMAPPED'
  AND fm.player_id IS NULL
GROUP BY p.first_name, p.last_name, p.birth, p.nation_name
HAVING COUNT(DISTINCT p.id) = 1
   AND COUNT(DISTINCT fm.fm_uid) = 1;
