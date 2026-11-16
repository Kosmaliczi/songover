package io.github.kosmaliczi.songover.catalog.album.domain.exception;

import io.github.kosmaliczi.songover.common.exception.BaseBusinessException;
import org.springframework.http.HttpStatus;

import java.util.UUID;

public class AlbumNotFoundException extends BaseBusinessException {

    public AlbumNotFoundException(UUID albumId) {
        super("Album with id " + albumId + " not found", HttpStatus.NOT_FOUND);
    }
}