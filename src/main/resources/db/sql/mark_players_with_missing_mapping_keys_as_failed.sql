UPDATE player p
SET p.mapping_status = 'FAILED',
    p.mapping_method = NULL
WHERE p.mapping_status <> 'FAILED'
  AND (
      p.first_name IS NULL
   OR p.last_name IS NULL
   OR p.birth IS NULL
   OR p.nation_name IS NULL
  );
