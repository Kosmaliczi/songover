package io.github.kosmaliczi.songover.media.infrastructure.audio;

public record AudioMetadata(
        String formatName,
        String codecName,
        int durationSeconds
) {
}
