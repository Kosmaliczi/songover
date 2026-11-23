CREATE SCHEMA IF NOT EXISTS media;

CREATE TABLE media.upload (
                              id           UUID                     PRIMARY KEY,
                              track_id     UUID                     NOT NULL,
                              object_key   VARCHAR(500)             NOT NULL,
                              content_type VARCHAR(100)             NOT NULL,
                              size_bytes   BIGINT                   NOT NULL,
                              status       VARCHAR(20)              NOT NULL,
                              created_at   TIMESTAMP WITH TIME ZONE NOT NULL,
                              uploaded_at  TIMESTAMP WITH TIME ZONE,
                              version      BIGINT                   NOT NULL DEFAULT 0,
                              CONSTRAINT uk_upload_object_key UNIQUE (object_key),
                              CONSTRAINT ck_upload_status CHECK (status IN ('PENDING_UPLOAD', 'UPLOADED')),
                              CONSTRAINT ck_upload_size_positive CHECK (size_bytes > 0)
);

CREATE INDEX idx_upload_track_id ON media.upload (track_id);