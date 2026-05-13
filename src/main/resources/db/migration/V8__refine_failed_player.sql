UPDATE player
SET mapping_status = "UNMAPPED"
WHERE mapping_status = "FAILED";

UPDATE player
SET mapping_status = 'FAILED'
WHERE first_name IS NULL
   OR last_name   IS NULL
   OR birth       IS NULL
   OR nation_name IS NULL;