package io.github.kosmaliczi.songover.media.upload.api.dto;

import java.time.Instant;
import java.util.UUID;

public record UploadUrlResponse(
        UUID uploadId,
        String uploadUrl,
        Instant expiresAt
) {
}