package io.github.kosmaliczi.songover.media.infrastructure.storage;

import java.time.Instant;

public record PresignedUpload(
        String url,
        Instant expiresAt
) {
}