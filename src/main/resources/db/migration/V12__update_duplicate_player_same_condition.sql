-- case2. 기본 매핑 조건(4키)이 같은 UNMAPPED player 가 2명 이상 존재하고,
--        해당 4키로 매핑 가능한 fmplayer 가 있는 경우
UPDATE player p1
JOIN player p2
  ON p1.first_name  = p2.first_name
 AND p1.last_name   = p2.last_name
 AND p1.birth       = p2.birth
 AND p1.nation_name = p2.nation_name
 AND p1.id != p2.id
SET p1.mapping_status = 'DUPLICATE'
WHERE p1.mapping_status = 'UNMAPPED'
  AND p2.mapping_status = 'UNMAPPED'
  AND EXISTS (
    SELECT 1 FROM fmplayer fm
    WHERE fm.first_name  = p1.first_name
      AND fm.last_name   = p1.last_name
      AND fm.birth       = p1.birth
      AND fm.nation_name = p1.nation_name
      AND fm.player_id IS NULL
  )
