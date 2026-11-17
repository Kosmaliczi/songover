ALTER TABLE catalog.track
    ADD COLUMN album_id     UUID,
    ADD COLUMN track_number INTEGER,
    ADD CONSTRAINT fk_track_album FOREIGN KEY (album_id) REFERENCES catalog.album (id),
    ADD CONSTRAINT ck_track_number_positive CHECK (track_number > 0),
    ADD CONSTRAINT ck_track_number_requires_album CHECK (track_number IS NULL OR album_id IS NOT NULL),
    ADD CONSTRAINT uk_track_album_number UNIQUE (album_id, track_number);

CREATE INDEX idx_track_album_id ON catalog.track (album_id);