package io.github.kosmaliczi.songover.catalog.artist.domain.exception;

public class ArtistAlreadyExistsException extends RuntimeException {

    public ArtistAlreadyExistsException(String name) {
        super("Artist with name '" + name + "' already exists");
    }
}
