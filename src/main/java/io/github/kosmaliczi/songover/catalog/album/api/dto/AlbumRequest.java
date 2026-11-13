package io.github.kosmaliczi.songover.catalog.album.api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;
import java.util.UUID;

public record AlbumRequest(
        @NotBlank(message = "Title is required")
        @Size(max = 200)
        String title,

        LocalDate releaseDate,

        @NotNull(message = "Artist id is required")
        UUID artistId
) {
}