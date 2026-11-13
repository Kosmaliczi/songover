package io.github.kosmaliczi.songover.catalog.album.api.dto;

import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

public record AlbumResponse(
        UUID id,
        String title,
        LocalDate releaseDate,
        UUID artistId,
        String artistName,
        Instant createdAt
) {
}