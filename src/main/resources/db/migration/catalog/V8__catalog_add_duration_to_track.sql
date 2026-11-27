ALTER TABLE catalog.track
    ADD COLUMN duration_seconds INTEGER;

ALTER TABLE catalog.track
    ADD CONSTRAINT ck_track_duration_positive CHECK (duration_seconds > 0);
