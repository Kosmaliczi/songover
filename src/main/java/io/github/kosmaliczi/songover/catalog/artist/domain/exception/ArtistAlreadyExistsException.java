package io.github.kosmaliczi.songover.catalog.artist.domain.exception;

import io.github.kosmaliczi.songover.common.exception.BaseBusinessException;
import org.springframework.http.HttpStatus;

public class ArtistAlreadyExistsException extends BaseBusinessException {

    public ArtistAlreadyExistsException(String name) {
        super("Artist with name '" + name + "' already exists", HttpStatus.CONFLICT);
    }
}