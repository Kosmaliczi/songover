package io.github.kosmaliczi.songover.media.infrastructure.audio;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.time.Duration;

@ConfigurationProperties(prefix = "songover.ffprobe")
public record FfprobeProperties(
        String path,
        Duration timeout
) {
}
