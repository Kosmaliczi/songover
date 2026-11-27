package io.github.kosmaliczi.songover.media.events;

import java.util.UUID;

public record AudioProcessed(UUID uploadId, UUID trackId, int durationSeconds) {
}
