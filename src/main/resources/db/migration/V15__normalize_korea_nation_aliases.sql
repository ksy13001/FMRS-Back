-- V14에서 추가된 SOUTH KOREA 정규화와 수동 SQL 적용 흔적을 포함해
-- player / fmplayer 양쪽을 NationNormalizer 기준 canonical 값으로 정렬한다.

UPDATE player p
SET p.nation_name = 'KOREA REPUBLIC'
WHERE p.nation_name IN (
    'SOUTH KOREA',
    'REPUBLIC KOR',
    'REPBULIC KOR',
    'Korea Republic'
);

UPDATE fmplayer fm
SET fm.nation_name = 'KOREA REPUBLIC'
WHERE fm.nation_name IN (
    'SOUTH KOREA',
    'REPUBLIC KOR',
    'REPBULIC KOR',
    'Korea Republic'
);
