package io.github.kosmaliczi.songover.catalog.track.api.dto;

import io.github.kosmaliczi.songover.catalog.track.domain.model.TrackStatus;

import java.time.Instant;
import java.util.UUID;

public record TrackResponse(
        UUID id,
        String title,
        boolean explicit,
        TrackStatus status,
        UUID artistId,
        String artistName,
        Instant createdAt
) {
}
