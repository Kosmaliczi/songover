package io.github.kosmaliczi.songover.media.upload.domain.exception;

import io.github.kosmaliczi.songover.common.exception.BaseBusinessException;
import org.springframework.http.HttpStatus;

import java.util.UUID;

public class UploadNotFoundException extends BaseBusinessException {

    public UploadNotFoundException(UUID uploadId) {
        super("Upload with id " + uploadId + " not found", HttpStatus.NOT_FOUND);
    }
}