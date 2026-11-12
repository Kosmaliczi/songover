package io.github.kosmaliczi.songover.catalog.track.domain.exception;

import java.util.UUID;

public class TrackNotFoundException extends RuntimeException {

    public TrackNotFoundException(UUID trackId) {
        super("Track with id " + trackId + " not found");
    }
}
