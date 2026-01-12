ALTER TABLE image_rooms
ADD COLUMN is_thumbnail BOOLEAN DEFAULT FALSE;

UPDATE image_rooms ir
SET is_thumbnail = TRUE
WHERE ir.image_id IN (
    SELECT MIN(image_id)
    FROM image_rooms
    GROUP BY room_id
);