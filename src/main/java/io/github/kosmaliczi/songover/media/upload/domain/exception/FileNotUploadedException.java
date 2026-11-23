package io.github.kosmaliczi.songover.media.upload.domain.exception;

import io.github.kosmaliczi.songover.common.exception.BaseBusinessException;
import org.springframework.http.HttpStatus;

import java.util.UUID;

public class FileNotUploadedException extends BaseBusinessException {

    public FileNotUploadedException(UUID uploadId) {
        super("File for upload " + uploadId + " is missing or does not match the declared size", HttpStatus.CONFLICT);
    }
}