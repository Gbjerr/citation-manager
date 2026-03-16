ALTER TABLE citations ADD COLUMN position INT;
UPDATE citations SET position = 1; -- or some logic
ALTER TABLE citations ALTER COLUMN position SET NOT NULL;