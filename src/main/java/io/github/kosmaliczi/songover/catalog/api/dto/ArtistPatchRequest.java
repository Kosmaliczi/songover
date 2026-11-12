package io.github.kosmaliczi.songover.catalog.api.dto;

import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record ArtistPatchRequest(
        @Size(min = 2, max = 100)
        @Pattern(regexp = ".*\\S.*", message = "Name must not be blank")
        String name,

        @Size(max = 1000)
        String description
) {
}