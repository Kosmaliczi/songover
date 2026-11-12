package io.github.kosmaliczi.songover.catalog.track.api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.UUID;

public record TrackRequest(
        @NotBlank(message = "Title is required")
        @Size(max = 200)
        String title,

        boolean explicit,

        @NotNull(message = "Artist id is required")
        UUID artistId
) {
}
