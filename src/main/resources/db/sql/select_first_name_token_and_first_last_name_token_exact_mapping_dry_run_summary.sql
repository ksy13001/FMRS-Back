WITH token_matches AS (
    SELECT
        MIN(p.id) AS player_id,
        MIN(fm1.fm_uid) AS fm_uid
    FROM player p
    JOIN fmplayer fm1
      ON SUBSTRING_INDEX(TRIM(p.first_name), ' ', 1) = SUBSTRING_INDEX(TRIM(fm1.first_name), ' ', 1)
     AND SUBSTRING_INDEX(TRIM(p.last_name), ' ', 1) = SUBSTRING_INDEX(TRIM(fm1.last_name), ' ', 1)
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
        SUBSTRING_INDEX(TRIM(p.last_name), ' ', 1),
        p.birth,
        p.nation_name
    HAVING COUNT(DISTINCT p.id) = 1
       AND COUNT(DISTINCT fm1.fm_uid) = 1
),
eligible_matches AS (
    SELECT token_match.player_id, token_match.fm_uid
    FROM token_matches token_match
    WHERE NOT EXISTS (
        SELECT 1
        FROM fmplayer linked
        WHERE linked.fm_uid = token_match.fm_uid
          AND linked.player_id IS NOT NULL
    )
),
matched_players AS (
    SELECT p.id
    FROM player p
    WHERE p.mapping_status = 'NO_MATCH'
      AND (
          EXISTS (
              SELECT 1
              FROM fmplayer fm
              WHERE fm.player_id = p.id
          )
          OR EXISTS (
              SELECT 1
              FROM eligible_matches eligible_match
              WHERE eligible_match.player_id = p.id
          )
      )
)
SELECT (
           SELECT COUNT(*)
           FROM matched_players
       ) AS matched_candidates,
       (
           SELECT COUNT(*)
           FROM fmplayer fm
           JOIN eligible_matches eligible_match ON eligible_match.fm_uid = fm.fm_uid
           WHERE fm.player_id IS NULL
       ) AS linked_fmplayer_rows;
