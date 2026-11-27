package io.github.kosmaliczi.songover.media.events;

import java.util.UUID;

public record AudioProcessingFailed(UUID uploadId, UUID trackId, String reason) {
}
