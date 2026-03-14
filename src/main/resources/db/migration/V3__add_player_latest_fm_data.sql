ALTER TABLE player
    ADD COLUMN latest_current_ability INT NULL,
    ADD COLUMN latest_potential_ability INT NULL,
    ADD COLUMN latest_fm_version VARCHAR(10) NULL;

UPDATE player p
    JOIN (
    SELECT fp1.player_id, fp1.current_ability, fp1.potential_ability, fp1.fm_version
    FROM fmplayer fp1
    WHERE fp1.fm_version = (
    SELECT fp2.fm_version FROM fmplayer fp2
    WHERE fp2.player_id = fp1.player_id
    ORDER BY fp2.fm_version DESC
    LIMIT 1
    )
    AND fp1.player_id IS NOT NULL
    ) latest
ON latest.player_id = p.id
    SET p.latest_current_ability = latest.current_ability,
        p.latest_potential_ability = latest.potential_ability,
        p.latest_fm_version = latest.fm_version;