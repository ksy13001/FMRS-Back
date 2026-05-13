-- 4키가 모두 있으나 구 매핑 로직(COUNT(*) > 1)으로 잘못 FAILED 처리된 선수 → UNMAPPED 로 리셋
-- (필수 데이터 누락으로 인한 정상 FAILED 는 건드리지 않음)
UPDATE player
SET mapping_status = 'UNMAPPED'
WHERE mapping_status = 'FAILED'
  AND first_name  IS NOT NULL
  AND last_name   IS NOT NULL
  AND birth       IS NOT NULL
  AND nation_name IS NOT NULL;


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