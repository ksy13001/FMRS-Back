-- 간단 검색 성능 최적화를 위한 인덱스 추가
-- last_name, first_name: startsWith (LIKE 'son%') 조건에서 B-tree 인덱스 활용
-- mapping_status: ORDER BY CASE WHEN 정렬 기준
-- (mapping_status, id) 복합 인덱스: cursor 페이지네이션 조건 최적화
ALTER TABLE player ADD INDEX idx_player_last_name         (last_name(20));
ALTER TABLE player ADD INDEX idx_player_first_name        (first_name(20));
ALTER TABLE player ADD INDEX idx_player_mapping_status    (mapping_status);
ALTER TABLE player ADD INDEX idx_player_mapping_status_id (mapping_status, id);
