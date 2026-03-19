-- NationNormalizer 와 동일한 치환 규칙을 기존 데이터에 적용
-- 적용 대상: player.nation_name, fmplayer.nation_name

-- -------------------------------------------------------
-- player 테이블
-- -------------------------------------------------------

-- Step 1. 전체 대문자 통일
UPDATE player
SET nation_name = UPPER(nation_name)
WHERE nation_name != UPPER(nation_name);

-- Step 2. 소스별 국가명 표기 차이 치환
UPDATE player
SET nation_name = CASE nation_name
    WHEN 'TURKEY'                    THEN 'TÜRKIYE'
    WHEN 'TÃ¼RKIYE'                  THEN 'TÜRKIYE'
    WHEN 'CZECH REPUBLIC'            THEN 'CZECHIA'
    WHEN 'FYR MACEDONIA'             THEN 'NORTH MACEDONIA'
    WHEN 'HONG KONG (CHINA PR)'      THEN 'HONG KONG, CHINA'
    WHEN 'KYRGYZSTAN'                THEN 'KYRGYZ REPUBLIC'
    WHEN 'ST. VINCENT / GRENADINES'  THEN 'ST. VINCENT AND THE GRENADINES'
    ELSE nation_name
END
WHERE nation_name IN (
    'TURKEY',
    'TÃ¼RKIYE',
    'CZECH REPUBLIC',
    'FYR MACEDONIA',
    'HONG KONG (CHINA PR)',
    'KYRGYZSTAN',
    'ST. VINCENT / GRENADINES'
);

-- -------------------------------------------------------
-- fmplayer 테이블
-- -------------------------------------------------------

-- Step 1. 전체 대문자 통일
UPDATE fmplayer
SET nation_name = UPPER(nation_name)
WHERE nation_name != UPPER(nation_name);

-- Step 2. 소스별 국가명 표기 차이 치환
UPDATE fmplayer
SET nation_name = CASE nation_name
    WHEN 'TURKEY'                    THEN 'TÜRKIYE'
    WHEN 'TÃ¼RKIYE'                  THEN 'TÜRKIYE'
    WHEN 'CZECH REPUBLIC'            THEN 'CZECHIA'
    WHEN 'FYR MACEDONIA'             THEN 'NORTH MACEDONIA'
    WHEN 'HONG KONG (CHINA PR)'      THEN 'HONG KONG, CHINA'
    WHEN 'KYRGYZSTAN'                THEN 'KYRGYZ REPUBLIC'
    WHEN 'ST. VINCENT / GRENADINES'  THEN 'ST. VINCENT AND THE GRENADINES'
    ELSE nation_name
END
WHERE nation_name IN (
    'TURKEY',
    'TÃ¼RKIYE',
    'CZECH REPUBLIC',
    'FYR MACEDONIA',
    'HONG KONG (CHINA PR)',
    'KYRGYZSTAN',
    'ST. VINCENT / GRENADINES'
);
