UPDATE fmplayer fm
JOIN (
    SELECT
        MIN(p.id) AS player_id,
        MIN(fm1.fm_uid) AS fm_uid
    FROM player p
    JOIN fmplayer fm1
      ON SUBSTRING_INDEX(TRIM(p.first_name), ' ', 1) = SUBSTRING_INDEX(TRIM(fm1.first_name), ' ', 1)
     AND SUBSTRING_INDEX(TRIM(p.last_name), ' ', -1) = SUBSTRING_INDEX(TRIM(fm1.last_name), ' ', -1)
     AND p.birth = fm1.birth
     AND p.nation_name = fm1.nation_name
    WHERE p.mapping_status = 'NO_MATCH'
      AND p.first_name IS NOT NULL
      AND p.last_name IS NOT NULL
      AND p.birth IS NOT NULL
      AND p.nation_name IS NOT NULL
      AND fm1.player_id IS NULL
    GROUP BY
        SUBSTRING_INDEX(TRIM(p.first_name), ' ', 1),
        SUBSTRING_INDEX(TRIM(p.last_name), ' ', -1),
        p.birth,
        p.nation_name
    HAVING COUNT(DISTINCT p.id) = 1
       AND COUNT(DISTINCT fm1.fm_uid) = 1
) token_match ON fm.fm_uid = token_match.fm_uid
LEFT JOIN (
    SELECT DISTINCT fm_uid
    FROM fmplayer
    WHERE player_id IS NOT NULL
) linked ON linked.fm_uid = fm.fm_uid
SET fm.player_id = token_match.player_id
WHERE fm.player_id IS NULL
  AND linked.fm_uid IS NULL;
