package io.github.kosmaliczi.songover.catalog.album.domain.exception;

import java.util.UUID;

public class AlbumNotFoundException extends RuntimeException {
    public AlbumNotFoundException(UUID albumId) {
        super("Album with id " + albumId + " not found");
    }
}
