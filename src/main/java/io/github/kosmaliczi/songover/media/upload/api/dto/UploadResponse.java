package io.github.kosmaliczi.songover.media.upload.api.dto;

import io.github.kosmaliczi.songover.media.upload.domain.model.UploadStatus;

import java.time.Instant;
import java.util.UUID;

public record UploadResponse(
        UUID id,
        UUID trackId,
        UploadStatus status,
        String contentType,
        long sizeBytes,
        Instant createdAt,
        Instant uploadedAt
) {
}