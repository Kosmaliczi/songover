package io.github.kosmaliczi.songover.catalog.api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ArtistRequest(
        @NotBlank
        @Size(min = 2, max = 100)
        String name,

        @Size(max = 1000)
        String description
) {}