package io.github.kosmaliczi.songover.media.upload.domain.exception;

import io.github.kosmaliczi.songover.common.exception.BaseBusinessException;
import org.springframework.http.HttpStatus;

import java.util.UUID;

public class UploadTrackNotFoundException extends BaseBusinessException {

    public UploadTrackNotFoundException(UUID trackId) {
        super("Track with id " + trackId + " not found", HttpStatus.NOT_FOUND);
    }
}
