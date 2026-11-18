package io.github.kosmaliczi.songover.media.infrastructure.storage;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.net.URI;
import java.time.Duration;

@ConfigurationProperties(prefix = "songover.storage")
public record StorageProperties(
        URI endpoint,
        String region,
        String accessKey,
        String secretKey,
        String bucket,
        Duration presignedUrlTtl
) {
}