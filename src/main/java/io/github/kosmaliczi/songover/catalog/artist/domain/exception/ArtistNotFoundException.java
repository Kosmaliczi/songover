package io.github.kosmaliczi.songover.catalog.artist.domain.exception;

import io.github.kosmaliczi.songover.common.exception.BaseBusinessException;
import org.springframework.http.HttpStatus;

import java.util.UUID;

public class ArtistNotFoundException extends BaseBusinessException {

    public ArtistNotFoundException(UUID artistId) {
        super("Artist with id " + artistId + " not found", HttpStatus.NOT_FOUND);
    }
}