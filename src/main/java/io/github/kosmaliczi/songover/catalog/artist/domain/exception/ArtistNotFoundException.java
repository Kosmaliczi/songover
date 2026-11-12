package io.github.kosmaliczi.songover.catalog.artist.domain.exception;

import java.util.UUID;

public class ArtistNotFoundException extends RuntimeException {

    public ArtistNotFoundException(UUID artistId) {
        super("Artist with id " + artistId + " not found");
    }
}
