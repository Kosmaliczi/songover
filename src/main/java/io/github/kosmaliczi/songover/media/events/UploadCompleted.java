package io.github.kosmaliczi.songover.media.events;

import java.util.UUID;

public record UploadCompleted(UUID uploadId, UUID trackId) {
}
