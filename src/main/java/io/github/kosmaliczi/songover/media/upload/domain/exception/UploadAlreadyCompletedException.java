package io.github.kosmaliczi.songover.media.upload.domain.exception;

import io.github.kosmaliczi.songover.common.exception.BaseBusinessException;
import org.springframework.http.HttpStatus;

import java.util.UUID;

public class UploadAlreadyCompletedException extends BaseBusinessException {

    public UploadAlreadyCompletedException(UUID uploadId) {
        super("Upload with id " + uploadId + " is already completed", HttpStatus.CONFLICT);
    }
}