package io.github.kosmaliczi.songover.media.infrastructure.audio;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
record FfprobeOutput(
        List<Stream> streams,
        Format format
) {

    @JsonIgnoreProperties(ignoreUnknown = true)
    record Stream(
            @JsonProperty("codec_type") String codecType,
            @JsonProperty("codec_name") String codecName
    ) {
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    record Format(
            @JsonProperty("format_name") String formatName,
            String duration
    ) {
    }
}
