CREATE TABLE catalog.album (
                               id           UUID                     PRIMARY KEY,
                               title        VARCHAR(200)             NOT NULL,
                               release_date DATE,
                               artist_id    UUID                     NOT NULL,
                               created_at   TIMESTAMP WITH TIME ZONE NOT NULL,
                               CONSTRAINT fk_album_artist FOREIGN KEY (artist_id) REFERENCES catalog.artist (id)
);

CREATE INDEX idx_album_artist_id ON catalog.album (artist_id);