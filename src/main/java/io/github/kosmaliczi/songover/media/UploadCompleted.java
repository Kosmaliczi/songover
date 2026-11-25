package io.github.kosmaliczi.songover.media;

import java.util.UUID;

public record UploadCompleted(UUID uploadId, UUID trackId) {
}
