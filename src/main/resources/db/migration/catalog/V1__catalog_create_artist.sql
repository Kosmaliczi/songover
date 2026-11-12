CREATE SCHEMA IF NOT EXISTS catalog;

CREATE TABLE catalog.artist (
                                id          UUID                     PRIMARY KEY,
                                name        VARCHAR(100)             NOT NULL,
                                description VARCHAR(1000),
                                created_at  TIMESTAMP WITH TIME ZONE NOT NULL,
                                CONSTRAINT uk_artist_name UNIQUE (name)
);