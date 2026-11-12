package io.github.kosmaliczi.songover.catalog.api.dto;

import java.time.Instant;
import java.util.UUID;

public record ArtistResponse (
        UUID id,
        String name,
        String description,
        Instant createdAt
) {}
