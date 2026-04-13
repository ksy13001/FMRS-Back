UPDATE fmplayer fm
JOIN (
    SELECT fm_uid, MIN(player_id) AS player_id
    FROM fmplayer
    WHERE player_id IS NOT NULL
    GROUP BY fm_uid
    HAVING COUNT(DISTINCT player_id) = 1
) mapped ON fm.fm_uid = mapped.fm_uid
SET fm.player_id = mapped.player_id
WHERE fm.player_id IS NULL;