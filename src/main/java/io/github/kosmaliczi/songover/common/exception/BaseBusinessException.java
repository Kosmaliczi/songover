package io.github.kosmaliczi.songover.common.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public abstract class BaseBusinessException extends RuntimeException {

    private final HttpStatus httpStatus;

    protected BaseBusinessException(String message, HttpStatus httpStatus) {
        super(message);
        this.httpStatus = httpStatus;
    }
}