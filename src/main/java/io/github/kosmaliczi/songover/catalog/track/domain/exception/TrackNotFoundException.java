package io.github.kosmaliczi.songover.catalog.track.domain.exception;

import io.github.kosmaliczi.songover.common.exception.BaseBusinessException;
import org.springframework.http.HttpStatus;

import java.util.UUID;

public class TrackNotFoundException extends BaseBusinessException {

    public TrackNotFoundException(UUID trackId) {
        super("Track with id " + trackId + " not found", HttpStatus.NOT_FOUND);
    }
}