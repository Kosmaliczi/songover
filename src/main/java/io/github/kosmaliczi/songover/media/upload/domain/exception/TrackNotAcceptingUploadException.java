package io.github.kosmaliczi.songover.media.upload.domain.exception;

import io.github.kosmaliczi.songover.common.exception.BaseBusinessException;
import org.springframework.http.HttpStatus;

import java.util.UUID;

public class TrackNotAcceptingUploadException extends BaseBusinessException {

    public TrackNotAcceptingUploadException(UUID trackId) {
        super("Track with id " + trackId + " does not accept uploads in its current status", HttpStatus.CONFLICT);
    }
}
