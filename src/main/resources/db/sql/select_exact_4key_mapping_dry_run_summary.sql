WITH duplicate_players AS (
    SELECT p.id
    FROM player p
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
),
exact_matches AS (
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
),
matched_players AS (
    SELECT p.id
    FROM player p
    WHERE p.mapping_status = 'UNMAPPED'
      AND NOT EXISTS (
          SELECT 1
          FROM duplicate_players duplicate_player
          WHERE duplicate_player.id = p.id
      )
      AND (
          EXISTS (
              SELECT 1
              FROM fmplayer fm
              WHERE fm.player_id = p.id
          )
          OR EXISTS (
              SELECT 1
              FROM exact_matches exact_match
              WHERE exact_match.player_id = p.id
          )
      )
)
SELECT (
           SELECT COUNT(*)
           FROM player p
           WHERE p.mapping_status <> 'FAILED'
             AND (
                 p.first_name IS NULL
              OR p.last_name IS NULL
              OR p.birth IS NULL
              OR p.nation_name IS NULL
             )
       ) AS failed_candidates,
       (
           SELECT COUNT(*)
           FROM duplicate_players
       ) AS duplicate_candidates,
       (
           SELECT COUNT(*)
           FROM matched_players
       ) AS exact_matched_candidates,
       (
           SELECT COUNT(*)
           FROM player p
           WHERE p.mapping_status = 'UNMAPPED'
             AND p.first_name IS NOT NULL
             AND p.last_name IS NOT NULL
             AND p.birth IS NOT NULL
             AND p.nation_name IS NOT NULL
             AND NOT EXISTS (
                 SELECT 1
                 FROM duplicate_players duplicate_player
                 WHERE duplicate_player.id = p.id
             )
             AND NOT EXISTS (
                 SELECT 1
                 FROM matched_players matched_player
                 WHERE matched_player.id = p.id
             )
       ) AS no_match_candidates,
       (
           SELECT COUNT(*)
           FROM fmplayer fm
           JOIN exact_matches exact_match ON exact_match.fm_uid = fm.fm_uid
           WHERE fm.player_id IS NULL
       ) AS linked_fmplayer_rows;
