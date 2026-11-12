package io.github.kosmaliczi.songover.catalog.track.api.dto;

import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record TrackPatchRequest(
        @Size(max = 200)
        @Pattern(regexp = ".*\\S.*", message = "Title must not be blank")
        String title,

        Boolean explicit
) {
}
