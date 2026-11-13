package io.github.kosmaliczi.songover.catalog.album.api.dto;

import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public record AlbumPatchRequest(
        @Size(max = 200)
        @Pattern(regexp = ".*\\S.*", message = "Title must not be blank")
        String title,

        LocalDate releaseDate
) {
}