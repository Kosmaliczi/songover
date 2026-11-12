CREATE TABLE catalog.track (
    id         UUID                     PRIMARY KEY,
    title      VARCHAR(200)             NOT NULL,
    explicit   BOOLEAN                  NOT NULL DEFAULT FALSE,
    status     VARCHAR(20)              NOT NULL,
    artist_id  UUID                     NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL,
    CONSTRAINT fk_track_artist FOREIGN KEY (artist_id) REFERENCES catalog.artist (id),
    CONSTRAINT ck_track_status CHECK (status IN ('DRAFT', 'PROCESSING', 'READY', 'FAILED', 'PUBLISHED', 'UNPUBLISHED', 'BLOCKED'))
);

CREATE INDEX idx_track_artist_id ON catalog.track (artist_id);
