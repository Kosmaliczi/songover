package io.github.kosmaliczi.songover.catalog.track.domain.exception;

import io.github.kosmaliczi.songover.catalog.track.domain.model.TrackStatus;
import io.github.kosmaliczi.songover.common.exception.BaseBusinessException;
import org.springframework.http.HttpStatus;

import java.util.UUID;

public class InvalidTrackStatusTransitionException extends BaseBusinessException {

    public InvalidTrackStatusTransitionException(UUID trackId, TrackStatus from, TrackStatus to) {
        super("Track " + trackId + " cannot change status from " + from + " to " + to, HttpStatus.CONFLICT);
    }
}