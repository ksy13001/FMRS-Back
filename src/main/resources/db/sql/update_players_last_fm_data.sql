UPDATE player p
JOIN (
    SELECT fm1.player_id,
           fm1.current_ability,
           fm1.potential_ability,
           fm1.fm_version,
           CASE WHEN fm1.goalkeeper >= 20 THEN TRUE ELSE FALSE END AS is_gk,
           CASE fm1.fm_version
               WHEN 'FM24' THEN 24
               WHEN 'FM26' THEN 26
           END AS fm_version_rank
    FROM fmplayer fm1
    LEFT JOIN fmplayer fm2
      ON fm2.player_id = fm1.player_id
     AND (
            CASE fm2.fm_version
                WHEN 'FM24' THEN 24
                WHEN 'FM26' THEN 26
            END >
            CASE fm1.fm_version
                WHEN 'FM24' THEN 24
                WHEN 'FM26' THEN 26
            END
            OR (
                CASE fm2.fm_version
                    WHEN 'FM24' THEN 24
                    WHEN 'FM26' THEN 26
                END =
                CASE fm1.fm_version
                    WHEN 'FM24' THEN 24
                    WHEN 'FM26' THEN 26
                END
                AND fm2.id > fm1.id
            )
        )
    WHERE fm1.player_id IS NOT NULL
      AND fm2.id IS NULL
) latest
  ON latest.player_id = p.id
SET p.latest_current_ability   = latest.current_ability,
    p.latest_potential_ability = latest.potential_ability,
    p.latest_fm_version        = latest.fm_version,
    p.is_gk                    = latest.is_gk
WHERE p.mapping_status = 'MATCHED'
  AND (
         p.latest_fm_version IS NULL
      OR COALESCE(
            CASE p.latest_fm_version
                WHEN 'FM24' THEN 24
                WHEN 'FM26' THEN 26
            END,
            0
         ) < latest.fm_version_rank
      OR COALESCE(p.latest_current_ability, -1) <> COALESCE(latest.current_ability, -1)
      OR COALESCE(p.latest_potential_ability, -1) <> COALESCE(latest.potential_ability, -1)
      OR COALESCE(p.is_gk, FALSE) <> latest.is_gk
  );
