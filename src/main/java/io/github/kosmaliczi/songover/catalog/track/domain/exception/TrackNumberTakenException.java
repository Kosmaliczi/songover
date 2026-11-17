package io.github.kosmaliczi.songover.catalog.track.domain.exception;

import io.github.kosmaliczi.songover.common.exception.BaseBusinessException;
import org.springframework.http.HttpStatus;

import java.util.UUID;

public class TrackNumberTakenException extends BaseBusinessException {

    public TrackNumberTakenException(UUID albumId, int trackNumber) {
        super("Track number " + trackNumber + " is already taken in album " + albumId, HttpStatus.CONFLICT);
    }
}