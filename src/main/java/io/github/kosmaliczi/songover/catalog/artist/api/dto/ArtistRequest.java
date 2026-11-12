package io.github.kosmaliczi.songover.catalog.artist.api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ArtistRequest(
        @NotBlank(message = "Name is required")
        @Size(min = 2, max = 100)
        String name,

        @Size(max = 1000)
        String description
) {
}
