package io.github.kosmaliczi.songover.media.infrastructure.audio;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(FfprobeProperties.class)
class FfprobeConfig {
}
