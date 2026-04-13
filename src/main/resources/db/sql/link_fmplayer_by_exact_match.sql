UPDATE fmplayer fm
JOIN (
    SELECT MIN(p.id) AS player_id, MIN(fm1.fm_uid) AS fm_uid
    FROM player p
    JOIN fmplayer fm1
      ON p.first_name  = fm1.first_name
     AND p.last_name   = fm1.last_name
     AND p.birth       = fm1.birth
     AND p.nation_name = fm1.nation_name
    WHERE p.mapping_status = 'UNMAPPED'
      AND fm1.player_id IS NULL
    GROUP BY p.first_name, p.last_name, p.birth, p.nation_name
    HAVING COUNT(DISTINCT p.id) = 1
       AND COUNT(DISTINCT fm1.fm_uid) = 1
) exact_match ON fm.fm_uid = exact_match.fm_uid
SET fm.player_id = exact_match.player_id
WHERE fm.player_id IS NULL;
