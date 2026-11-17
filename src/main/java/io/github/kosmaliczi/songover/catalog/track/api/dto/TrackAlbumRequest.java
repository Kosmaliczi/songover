package io.github.kosmaliczi.songover.catalog.track.api.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.util.UUID;

public record TrackAlbumRequest(
        @NotNull(message = "Album id is required")
        UUID albumId,

        @NotNull(message = "Track number is required")
        @Positive(message = "Track number must be greater than 0")
        Integer trackNumber
) {
}